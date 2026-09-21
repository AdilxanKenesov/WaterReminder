package com.visionsystems.waterreminder.presenter.screens.profile

import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.EditProfileRoute
import com.visionsystems.waterreminder.navigation.SetGoalRoute
import com.visionsystems.waterreminder.navigation.SignInRoute
import javax.inject.Inject

class ProfileDirection @Inject constructor(
    private val navigator: AppNavigator
) : ProfileContract.Direction {

    override fun openEditProfile() {
        navigator.navigateTo(EditProfileRoute)
    }

    override fun openSetGoal() {
        navigator.navigateTo(SetGoalRoute)
    }

    override fun openSignIn() {
        navigator.replaceAll(SignInRoute)
    }
}
