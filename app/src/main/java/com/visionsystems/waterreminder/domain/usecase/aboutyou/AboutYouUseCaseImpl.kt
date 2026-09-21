package com.visionsystems.waterreminder.domain.usecase.aboutyou

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.domain.module.UserProfileUiData
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.util.ReminderPreview
import javax.inject.Inject

class AboutYouUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val currentUser: CurrentUserProvider
) : AboutYouUseCase {

    override suspend fun loadProfile(): UserProfileUiData? = profileRepository.getProfile()

    override suspend fun saveBasics(gender: Gender, age: Int, weightKg: Int, activityLevel: ActivityLevel) {
        val existing = profileRepository.getProfile()
        val user = authRepository.currentUser.value
        val profile = existing?.copy(
            gender = gender,
            age = age,
            weightKg = weightKg,
            activityLevel = activityLevel
        ) ?: UserProfileUiData(
            uid = currentUser.currentUid.value,
            fullName = user?.displayName.orEmpty(),
            email = user?.email.orEmpty(),
            gender = gender,
            age = age,
            weightKg = weightKg,
            heightCm = 0,
            wakeTime = ReminderPreview.DEFAULT_WAKE,
            sleepTime = ReminderPreview.DEFAULT_SLEEP,
            activityLevel = activityLevel
        )
        profileRepository.saveProfile(profile)
    }

    override fun signOut() {
        authRepository.signOut()
    }
}
