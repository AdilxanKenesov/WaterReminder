package com.visionsystems.waterreminder.domain.usecase.goalready

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.GoalSummaryUiData
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import javax.inject.Inject

class GoalReadyUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val goalRepository: GoalRepository,
    private val cupRepository: CupRepository,
    private val reminderScheduler: ReminderScheduler
) : GoalReadyUseCase {

    override suspend fun prepareGoal(): GoalSummaryUiData {
        val profile = profileRepository.getProfile()
        val goal = goalRepository.recommendedGoal()
        goalRepository.setTodayGoal(goal)
        reminderScheduler.rescheduleAll()
        return GoalSummaryUiData(
            goalMl = goal,
            weightKg = profile?.weightKg ?: 0,
            activityLevel = profile?.activityLevel ?: ActivityLevel.MODERATE,
            cupMl = cupRepository.getSelectedCupMl()
        )
    }
}
