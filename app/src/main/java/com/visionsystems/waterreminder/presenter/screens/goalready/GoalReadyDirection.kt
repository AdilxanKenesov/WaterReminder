package com.visionsystems.waterreminder.presenter.screens.goalready

import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.MainRoute
import com.visionsystems.waterreminder.navigation.SetGoalRoute
import javax.inject.Inject

class GoalReadyDirection @Inject constructor(
    private val navigator: AppNavigator
) : GoalReadyContract.Direction {

    override fun openMain() {
        navigator.replaceAll(MainRoute)
    }

    override fun openSetGoal() {
        navigator.replaceAll(MainRoute)
        navigator.navigateTo(SetGoalRoute)
    }
}
