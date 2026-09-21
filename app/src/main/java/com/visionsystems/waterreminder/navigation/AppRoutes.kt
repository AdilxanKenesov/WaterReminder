package com.visionsystems.waterreminder.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute : NavKey

@Serializable
data object SignInRoute : NavKey

@Serializable
data object SignUpRoute : NavKey

@Serializable
data object VerifyEmailRoute : NavKey

@Serializable
data object AboutYouRoute : NavKey

@Serializable
data object ScheduleRoute : NavKey

@Serializable
data object GoalReadyRoute : NavKey

@Serializable
data object MainRoute : NavKey

@Serializable
data object SetGoalRoute : NavKey

@Serializable
data object EditProfileRoute : NavKey

@Serializable
data object AddWaterRoute : NavKey

@Serializable
data object GoalReachedRoute : NavKey
