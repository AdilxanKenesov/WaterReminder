package com.visionsystems.waterreminder.presenter.screens.onboarding

import com.visionsystems.waterreminder.navigation.AppNavigator
import com.visionsystems.waterreminder.navigation.SignInRoute
import javax.inject.Inject

class OnboardingDirection @Inject constructor(
    private val navigator: AppNavigator
) : OnboardingContract.Direction {

    override fun openSignIn() {
        navigator.replaceAll(SignInRoute)
    }
}
