package com.visionsystems.waterreminder.presenter.screens.home

import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.GoalReachedRoute
import javax.inject.Inject

class HomeDirection @Inject constructor(
    private val navigator: AppNavigator
) : HomeContract.Direction {

    override fun openGoalReached() {
        navigator.navigateTo(GoalReachedRoute)
    }
}
