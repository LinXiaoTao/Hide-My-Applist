package com.box.android.black.ui.fragment

import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.box.android.black.service.ConfigManager
import com.box.android.black.ui.adapter.AppScopeAdapter
import com.box.android.black.ui.util.navController

class ScopeFragment : AppSelectFragment() {

    private lateinit var checked: MutableSet<String>

    override val firstComparator: Comparator<String> = Comparator.comparing { !checked.contains(it) }

    override val adapter by lazy {
        val args by navArgs<ScopeFragmentArgs>()
        checked = args.checked.toMutableSet()
        if (!args.filterOnlyEnabled) AppScopeAdapter(checked, null)
        else AppScopeAdapter(checked) { ConfigManager.getAppConfig(it)?.useWhitelist == args.isWhiteList }
    }

    override fun onBack() {
        setFragmentResult("app_select", Bundle().apply {
            putStringArrayList("checked", ArrayList(checked))
        })
        navController.navigateUp()
    }
}
