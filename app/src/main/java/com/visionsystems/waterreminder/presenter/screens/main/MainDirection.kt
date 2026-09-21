package com.visionsystems.waterreminder.presenter.screens.main

import com.visionsystems.waterreminder.navigation.AddWaterRoute
import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.MainTab
import javax.inject.Inject

class MainDirection @Inject constructor(
    private val navigator: AppNavigator
) : MainContract.Direction {

    override fun selectTab(tab: MainTab) {
        navigator.selectTab(tab)
    }

    override fun openAddWater() {
        navigator.navigateTo(AddWaterRoute)
    }
}
