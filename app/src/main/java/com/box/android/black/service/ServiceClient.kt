package com.box.android.black.service

import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.Parcel
import android.os.RemoteException
import android.os.ServiceManager
import android.util.Log
import com.box.android.black.common.Constants
import com.box.android.black.common.IBlackService
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object ServiceClient : IBlackService, DeathRecipient {

    private const val TAG = "ServiceClient"

    private class ServiceProxy(private val obj: IBlackService) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method, args: Array<out Any?>?): Any? {
            val result = method.invoke(obj, *args.orEmpty())
            if (result == null) Log.i(TAG, "Call service method ${method.name}")
            else Log.i(TAG, "Call service method ${method.name} with result " + result.toString().take(20))
            return result
        }
    }

    @Volatile
    private var service: IBlackService? = null

    fun linkService(binder: IBinder) {
        service = Proxy.newProxyInstance(
            javaClass.classLoader,
            arrayOf(IBlackService::class.java),
            ServiceProxy(IBlackService.Stub.asInterface(binder))
        ) as IBlackService
        binder.linkToDeath(this, 0)
    }

    private fun getServiceLegacy(): IBlackService? {
        if (service != null) return service
        val pm = ServiceManager.getService("package")
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        val remote = try {
            data.writeInterfaceToken(Constants.DESCRIPTOR)
            data.writeInt(Constants.ACTION_GET_BINDER)
            pm.transact(Constants.TRANSACTION, data, reply, 0)
            reply.readException()
            val binder = reply.readStrongBinder()
            IBlackService.Stub.asInterface(binder)
        } catch (e: RemoteException) {
            Log.d(TAG, "Failed to get binder")
            null
        } finally {
            data.recycle()
            reply.recycle()
        }
        if (remote != null) {
            Log.i(TAG, "Binder acquired")
            remote.asBinder().linkToDeath(this, 0)
            service = Proxy.newProxyInstance(
                javaClass.classLoader,
                arrayOf(IBlackService::class.java),
                ServiceProxy(remote)
            ) as IBlackService
        }
        return service
    }

    override fun binderDied() {
        service = null
        Log.e(TAG, "Binder died")
    }

    override fun asBinder() = service?.asBinder()

    override fun getServiceVersion() = getServiceLegacy()?.serviceVersion ?: 0

    override fun getFilterCount() = getServiceLegacy()?.filterCount ?: 0

    override fun getLogs() = getServiceLegacy()?.logs

    override fun clearLogs() {
        getServiceLegacy()?.clearLogs()
    }

    override fun syncConfig(json: String) {
        getServiceLegacy()?.syncConfig(json)
    }

    override fun stopService(cleanEnv: Boolean) {
        getServiceLegacy()?.stopService(cleanEnv)
    }
}
