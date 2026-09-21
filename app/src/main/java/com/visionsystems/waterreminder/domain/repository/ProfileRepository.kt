package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.UserProfileUiData
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<UserProfileUiData?>
    suspend fun getProfile(): UserProfileUiData?
    suspend fun saveProfile(profile: UserProfileUiData)
    suspend fun savePhoto(sourceUri: String): String?
    suspend fun removePhoto()
    suspend fun deleteAllUserData()
}
