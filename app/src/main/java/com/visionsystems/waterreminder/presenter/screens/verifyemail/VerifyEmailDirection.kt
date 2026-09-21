package com.visionsystems.waterreminder.presenter.screens.verifyemail

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.SignInRoute
import com.visionsystems.waterreminder.navigation.toRoute
import javax.inject.Inject

class VerifyEmailDirection @Inject constructor(
    private val navigator: AppNavigator
) : VerifyEmailContract.Direction {

    override fun openStart(start: AppStart) {
        navigator.replaceAll(start.toRoute())
    }

    override fun openSignIn() {
        navigator.replaceAll(SignInRoute)
    }
}
