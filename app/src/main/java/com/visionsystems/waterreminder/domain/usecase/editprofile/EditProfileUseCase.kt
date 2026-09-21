package com.visionsystems.waterreminder.domain.usecase.editprofile

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.EditProfileUiData
import com.visionsystems.waterreminder.domain.module.Gender

interface EditProfileUseCase {
    suspend fun load(): EditProfileUiData?
    suspend fun setPhoto(sourceUri: String): Result<String?>
    suspend fun removePhoto(): String?
    suspend fun save(fullName: String, gender: Gender, age: Int, weightKg: Int, activityLevel: ActivityLevel)
}
