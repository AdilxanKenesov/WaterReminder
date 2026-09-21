package com.visionsystems.waterreminder.domain.module

import java.time.LocalTime

data class UserProfileUiData(
    val uid: String,
    val fullName: String,
    val email: String,
    val gender: Gender,
    val age: Int,
    val weightKg: Int,
    val heightCm: Int,
    val wakeTime: LocalTime,
    val sleepTime: LocalTime,
    val activityLevel: ActivityLevel,
    val photoPath: String? = null
)
