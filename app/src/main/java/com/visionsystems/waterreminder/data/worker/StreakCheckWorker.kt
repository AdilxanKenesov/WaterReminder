package com.visionsystems.waterreminder.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.NotificationRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.repository.StatsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class StreakCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val profileRepository: ProfileRepository,
    private val reminderRepository: ReminderRepository,
    private val goalRepository: GoalRepository,
    private val drinkRepository: DrinkRepository,
    private val statsRepository: StatsRepository,
    private val cupRepository: CupRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
    private val currentUser: CurrentUserProvider,
    private val scheduler: ReminderScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val profile = profileRepository.getProfile()
        val config = reminderRepository.getConfig()
        if (profile != null && config.streakNotify) {
            goalRepository.ensureGoalForToday()
            val today = drinkRepository.getToday()
            val uid = currentUser.currentUid.value
            if (!today.isCompleted && !settingsRepository.wasStreakNotified(uid, today.dayKey)) {
                val streak = statsRepository.getCurrentStreak()
                if (streak >= MIN_STREAK_TO_PROTECT) {
                    notificationRepository.showStreakAtRisk(streak, today.remainingMl, cupRepository.getSelectedCupMl())
                    settingsRepository.markStreakNotified(uid, today.dayKey)
                }
            }
        }
        scheduler.scheduleNextStreakCheck()
        return Result.success()
    }

    private companion object {
        const val MIN_STREAK_TO_PROTECT = 2
    }
}
