package com.visionsystems.waterreminder.domain.usecase.editprofile

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.EditProfileUiData
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class EditProfileUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : EditProfileUseCase {

    override suspend fun load(): EditProfileUiData? {
        val profile = profileRepository.getProfile() ?: return null
        val user = authRepository.currentUser.value
        return EditProfileUiData(
            profile = profile.copy(fullName = profile.fullName.ifBlank { user?.displayName.orEmpty() }),
            photo = profile.photoPath ?: user?.photoUrl
        )
    }

    override suspend fun setPhoto(sourceUri: String): Result<String?> =
        try {
            Result.success(withTimeout(PHOTO_TIMEOUT_MS) { profileRepository.savePhoto(sourceUri) })
        } catch (e: TimeoutCancellationException) {
            Result.failure(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(e)
        }

    override suspend fun removePhoto(): String? {
        profileRepository.removePhoto()
        return authRepository.currentUser.value?.photoUrl
    }

    override suspend fun save(fullName: String, gender: Gender, age: Int, weightKg: Int, activityLevel: ActivityLevel) {
        val profile = profileRepository.getProfile() ?: return
        profileRepository.saveProfile(
            profile.copy(
                fullName = fullName.trim(),
                gender = gender,
                age = age,
                weightKg = weightKg,
                activityLevel = activityLevel
            )
        )
    }

    private companion object {
        const val PHOTO_TIMEOUT_MS = 20_000L
    }
}
