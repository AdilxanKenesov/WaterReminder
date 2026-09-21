package com.visionsystems.waterreminder.domain.usecase.profile

import com.visionsystems.waterreminder.domain.module.ProfileOverviewUiData
import com.visionsystems.waterreminder.domain.module.WaterUnit
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.repository.WidgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ProfileUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val goalRepository: GoalRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val widgetRepository: WidgetRepository
) : ProfileUseCase {

    override fun observeProfile(): Flow<ProfileOverviewUiData> =
        combine(
            profileRepository.observeProfile(),
            authRepository.currentUser,
            goalRepository.observeTodayGoal(),
            settingsRepository.getUnit()
        ) { profile, user, goal, unit ->
            ProfileOverviewUiData(
                profile = profile,
                displayName = profile?.fullName?.takeIf { it.isNotBlank() } ?: user?.displayName.orEmpty(),
                email = user?.email ?: profile?.email.orEmpty(),
                photo = profile?.photoPath ?: user?.photoUrl,
                goalMl = goal,
                recommendedMl = goalRepository.recommendedGoal(),
                unit = unit
            )
        }

    override fun setUnit(unit: WaterUnit) {
        settingsRepository.setUnit(unit)
        widgetRepository.refresh()
    }

    override fun setLanguage(tag: String) {
        settingsRepository.setLanguage(tag)
        widgetRepository.refresh()
    }

    override fun signOut() {
        authRepository.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> {
        profileRepository.deleteAllUserData()
        return authRepository.deleteAccount()
    }
}
