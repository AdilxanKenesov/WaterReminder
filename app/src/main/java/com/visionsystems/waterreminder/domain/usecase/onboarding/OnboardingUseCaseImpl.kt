package com.visionsystems.waterreminder.domain.usecase.onboarding

import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import javax.inject.Inject

class OnboardingUseCaseImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : OnboardingUseCase {

    override fun completeOnboarding() {
        settingsRepository.setOnboardingDone(true)
    }
}
