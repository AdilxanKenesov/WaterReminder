package com.visionsystems.waterreminder.presenter.screens.schedule

import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.GoalReadyRoute
import javax.inject.Inject

class ScheduleDirection @Inject constructor(
    private val navigator: AppNavigator
) : ScheduleContract.Direction {

    override fun openGoalReady() {
        navigator.replaceAll(GoalReadyRoute)
    }

    override fun back() {
        navigator.back()
    }
}
