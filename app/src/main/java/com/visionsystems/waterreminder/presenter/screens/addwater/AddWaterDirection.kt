package com.visionsystems.waterreminder.presenter.screens.addwater

import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.GoalReachedRoute
import javax.inject.Inject

class AddWaterDirection @Inject constructor(
    private val navigator: AppNavigator
) : AddWaterContract.Direction {

    override fun close() {
        navigator.back()
    }

    override fun openGoalReached() {
        navigator.replaceTo(GoalReachedRoute)
    }
}
