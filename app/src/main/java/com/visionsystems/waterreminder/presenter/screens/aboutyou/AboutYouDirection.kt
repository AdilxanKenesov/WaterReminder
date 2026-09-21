package com.visionsystems.waterreminder.presenter.screens.aboutyou

import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.ScheduleRoute
import com.visionsystems.waterreminder.navigation.SignInRoute
import javax.inject.Inject

class AboutYouDirection @Inject constructor(
    private val navigator: AppNavigator
) : AboutYouContract.Direction {

    override fun openSchedule() {
        navigator.navigateTo(ScheduleRoute)
    }

    override fun openSignIn() {
        navigator.replaceAll(SignInRoute)
    }
}
