package com.visionsystems.waterreminder.presenter.screens.signup

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.VerifyEmailRoute
import com.visionsystems.waterreminder.navigation.toRoute
import javax.inject.Inject

class SignUpDirection @Inject constructor(
    private val navigator: AppNavigator
) : SignUpContract.Direction {

    override fun openVerifyEmail() {
        navigator.replaceAll(VerifyEmailRoute)
    }

    override fun openStart(start: AppStart) {
        navigator.replaceAll(start.toRoute())
    }

    override fun back() {
        navigator.back()
    }
}
