package com.visionsystems.waterreminder.presenter.screens.signin

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.SignUpRoute
import com.visionsystems.waterreminder.navigation.toRoute
import javax.inject.Inject

class SignInDirection @Inject constructor(
    private val navigator: AppNavigator
) : SignInContract.Direction {

    override fun openSignUp() {
        navigator.navigateTo(SignUpRoute)
    }

    override fun openStart(start: AppStart) {
        navigator.replaceAll(start.toRoute())
    }
}
