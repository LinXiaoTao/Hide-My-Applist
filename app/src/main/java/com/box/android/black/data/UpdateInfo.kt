package com.box.android.black.data

import com.box.android.black.common.Constants
import com.box.android.black.service.PrefManager
import kotlinx.serialization.Serializable
import rxhttp.*
import rxhttp.wrapper.param.RxHttp
import java.util.*

class UpdateInfo(
    val versionName: String,
    val versionCode: Int,
    val content: String,
    val downloadUrl: String
)

@Serializable
private data class UpdateData(
    val release: Item?,
    val beta: Item?
) {
    @Serializable
    data class Item(
        val versionName: String,
        val versionCode: Int,
        val downloadUrl: String
    )
}

suspend fun fetchLatestUpdate(): UpdateInfo? {
    val updateData = RxHttp.get(Constants.UPDATE_URL_BASE + "updates.json")
        .toAwait<UpdateData>()
        .tryAwait() ?: return null
    val isBeta = PrefManager.receiveBetaUpdate && updateData.beta != null
    val item = (if (isBeta) updateData.beta else updateData.release) ?: return null
    val variantPrefix = if (isBeta) "beta" else "release"
    val languagePrefix = if (Locale.getDefault().language.contains("zh")) "zh" else "en"
    val content = RxHttp.get(Constants.UPDATE_URL_BASE + variantPrefix + "-" + languagePrefix + ".html")
        .toAwaitString()
        .tryAwait() ?: return null
    return UpdateInfo(item.versionName, item.versionCode, content, item.downloadUrl)
}
