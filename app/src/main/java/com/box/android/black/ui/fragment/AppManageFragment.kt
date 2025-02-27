package com.box.android.black.ui.fragment

import android.os.Bundle
import com.google.android.material.transition.MaterialSharedAxis
import com.box.android.black.R
import com.box.android.black.service.ConfigManager
import com.box.android.black.ui.adapter.AppManageAdapter
import com.box.android.black.ui.util.navController

class AppManageFragment : AppSelectFragment() {

    override val firstComparator: Comparator<String> = Comparator.comparing(ConfigManager::isHideEnabled).reversed()

    override val adapter = AppManageAdapter {
        val args = AppSettingsFragmentArgs(it)
        navController.navigate(R.id.nav_app_settings, args.toBundle())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }
}
