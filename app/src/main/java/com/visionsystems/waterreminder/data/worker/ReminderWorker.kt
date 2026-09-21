package com.visionsystems.waterreminder.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.NotificationRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.util.ReminderPlanner
import com.visionsystems.waterreminder.domain.util.TimeProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val profileRepository: ProfileRepository,
    private val reminderRepository: ReminderRepository,
    private val goalRepository: GoalRepository,
    private val drinkRepository: DrinkRepository,
    private val cupRepository: CupRepository,
    private val notificationRepository: NotificationRepository,
    private val scheduler: ReminderScheduler,
    private val time: TimeProvider
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val profile = profileRepository.getProfile()
        val config = reminderRepository.getConfig()
        if (profile != null && config.enabled && ReminderPlanner.isAwake(time.now(), profile.wakeTime, profile.sleepTime)) {
            goalRepository.ensureGoalForToday()
            val today = drinkRepository.getToday()
            if (!today.isCompleted) {
                notificationRepository.showReminder(
                    consumedMl = today.consumedMl,
                    goalMl = today.goalMl,
                    cupMl = cupRepository.getSelectedCupMl(),
                    silent = !config.soundEnabled
                )
            }
        }
        scheduler.scheduleNextReminder()
        return Result.success()
    }
}
