package com.visionsystems.waterreminder.presenter.screens.setgoal

import com.visionsystems.waterreminder.navigation.AppNavigator
import javax.inject.Inject

class SetGoalDirection @Inject constructor(
    private val navigator: AppNavigator
) : SetGoalContract.Direction {

    override fun back() {
        navigator.back()
    }
}
