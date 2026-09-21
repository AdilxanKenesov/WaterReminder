package com.visionsystems.waterreminder.domain.usecase.aboutyou

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.domain.module.UserProfileUiData

interface AboutYouUseCase {
    suspend fun loadProfile(): UserProfileUiData?
    suspend fun saveBasics(gender: Gender, age: Int, weightKg: Int, activityLevel: ActivityLevel)
    fun signOut()
}
