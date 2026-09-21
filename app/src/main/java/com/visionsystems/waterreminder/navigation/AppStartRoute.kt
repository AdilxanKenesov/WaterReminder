package com.visionsystems.waterreminder.navigation

import androidx.navigation3.runtime.NavKey
import com.visionsystems.waterreminder.domain.module.AppStart

fun AppStart.toRoute(): NavKey = when (this) {
    AppStart.ONBOARDING -> OnboardingRoute
    AppStart.SIGN_IN -> SignInRoute
    AppStart.VERIFY_EMAIL -> VerifyEmailRoute
    AppStart.PROFILE_SETUP -> AboutYouRoute
    AppStart.MAIN -> MainRoute
}
