package com.box.android.black.plugin.hook

interface IFrameworkHook {

    fun load()
    fun unload()
    fun onConfigChanged() {}
}
