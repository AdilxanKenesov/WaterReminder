package com.visionsystems.waterreminder.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.NotificationRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.repository.WidgetRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MidnightWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val goalRepository: GoalRepository,
    private val notificationRepository: NotificationRepository,
    private val scheduler: ReminderScheduler,
    private val widgetRepository: WidgetRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        goalRepository.ensureGoalForToday()
        notificationRepository.cancelReminder()
        scheduler.scheduleNextReminder()
        scheduler.scheduleNextStreakCheck()
        scheduler.scheduleNextMidnight()
        widgetRepository.refresh()
        return Result.success()
    }
}
