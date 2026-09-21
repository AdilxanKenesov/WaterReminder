package com.visionsystems.waterreminder.domain.usecase.common

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class StartResolver @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
    private val currentUser: CurrentUserProvider
) {

    suspend fun resolve(): AppStart {
        if (!settingsRepository.isOnboardingDone()) return AppStart.ONBOARDING
        val user = authRepository.currentUser.value ?: return AppStart.SIGN_IN
        if (!user.isGoogleAccount && !user.isEmailVerified) return AppStart.VERIFY_EMAIL
        withTimeoutOrNull(UID_SYNC_TIMEOUT_MS) { currentUser.currentUid.first { it == user.uid } }
        return if (profileRepository.getProfile() == null) AppStart.PROFILE_SETUP else AppStart.MAIN
    }

    private companion object {
        const val UID_SYNC_TIMEOUT_MS = 3_000L
    }
}
