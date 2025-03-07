package com.box.android.black.plugin

import android.content.pm.IPackageManager
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.*
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import com.box.android.black.common.Constants
import kotlin.concurrent.thread

private const val TAG = "Black-PluginEntry"

@Suppress("unused")
class PluginEntry : IXposedHookZygoteInit, IXposedHookLoadPackage {

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelperInit.initZygote(startupParam)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == Constants.APP_PACKAGE_NAME) {
            EzXHelperInit.initHandleLoadPackage(lpparam)
            hookAllConstructorAfter("com.box.android.black.MyApp") {
                getFieldByDesc("Lcom/box/android/black/MyApp;->isHooked:Z").setBoolean(it.thisObject, true)
            }
        } else if (lpparam.packageName == "android") {
            EzXHelperInit.initHandleLoadPackage(lpparam)
            logI(TAG, "Hook entry")

            var serviceManagerHook: XC_MethodHook.Unhook? = null
            serviceManagerHook = findMethod("android.os.ServiceManager") {
                name == "addService"
            }.hookBefore { param ->
                if (param.args[0] == "package") {
                    serviceManagerHook?.unhook()
                    val pms = param.args[1] as IPackageManager
                    logD(TAG, "Got pms: $pms")
                    thread {
                        runCatching {
                            UserService.register(pms)
                            logI(TAG, "User service started")
                        }.onFailure {
                            logE(TAG, "System service crashed", it)
                        }
                    }
                }
            }
        }
    }
}
