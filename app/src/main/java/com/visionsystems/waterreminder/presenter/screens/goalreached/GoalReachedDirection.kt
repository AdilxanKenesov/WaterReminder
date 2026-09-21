package com.visionsystems.waterreminder.presenter.screens.goalreached

import com.visionsystems.waterreminder.navigation.AppNavigator
import javax.inject.Inject

class GoalReachedDirection @Inject constructor(
    private val navigator: AppNavigator
) : GoalReachedContract.Direction {

    override fun back() {
        navigator.back()
    }
}
