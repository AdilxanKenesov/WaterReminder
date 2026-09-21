package com.visionsystems.waterreminder.domain.usecase.schedule

import com.visionsystems.waterreminder.domain.module.SchedulePlanUiData
import com.visionsystems.waterreminder.domain.module.UserProfileUiData
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.util.ReminderPlanner
import java.time.LocalTime
import javax.inject.Inject

class ScheduleUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val goalRepository: GoalRepository,
    private val cupRepository: CupRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) : ScheduleUseCase {

    override suspend fun loadProfile(): UserProfileUiData? = profileRepository.getProfile()

    override suspend fun previewPlan(wake: LocalTime, sleep: LocalTime): SchedulePlanUiData {
        val interval = ReminderPlanner.intervalMinutes(
            wake = wake,
            sleep = sleep,
            goalMl = goalRepository.recommendedGoal(),
            cupMl = cupRepository.getSelectedCupMl(),
            overrideMinutes = reminderRepository.getConfig().intervalMinutes
        )
        return SchedulePlanUiData(intervalMinutes = interval, slots = ReminderPlanner.slots(wake, sleep, interval))
    }

    override suspend fun saveTimes(wake: LocalTime, sleep: LocalTime) {
        val profile = profileRepository.getProfile() ?: return
        profileRepository.saveProfile(profile.copy(wakeTime = wake, sleepTime = sleep))
        reminderScheduler.rescheduleAll()
    }
}
