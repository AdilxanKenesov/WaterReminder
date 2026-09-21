package com.visionsystems.waterreminder.data.worker

import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.util.ReminderPlanner
import com.visionsystems.waterreminder.domain.util.TimeProvider
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderSchedulerImpl @Inject constructor(
    private val workManager: WorkManager,
    private val profileRepository: ProfileRepository,
    private val reminderRepository: ReminderRepository,
    private val goalRepository: GoalRepository,
    private val cupRepository: CupRepository,
    private val time: TimeProvider
) : ReminderScheduler {

    override suspend fun ensureScheduled() {
        scheduleReminder(ExistingWorkPolicy.KEEP)
        scheduleStreakCheck(ExistingWorkPolicy.KEEP)
        scheduleMidnight(ExistingWorkPolicy.KEEP)
    }

    override suspend fun rescheduleAll() {
        scheduleReminder(ExistingWorkPolicy.REPLACE)
        scheduleStreakCheck(ExistingWorkPolicy.REPLACE)
        scheduleMidnight(ExistingWorkPolicy.REPLACE)
    }

    override suspend fun scheduleNextReminder() = scheduleReminder(ExistingWorkPolicy.REPLACE)

    override suspend fun scheduleNextStreakCheck() = scheduleStreakCheck(ExistingWorkPolicy.REPLACE)

    override fun scheduleNextMidnight() = scheduleMidnight(ExistingWorkPolicy.REPLACE)

    override fun snooze(minutes: Long) {
        enqueue(WORK_REMINDER, ReminderWorker::class.java, TimeUnit.MINUTES.toMillis(minutes), ExistingWorkPolicy.REPLACE)
    }

    override fun cancelAll() {
        workManager.cancelUniqueWork(WORK_REMINDER)
        workManager.cancelUniqueWork(WORK_STREAK_CHECK)
        workManager.cancelUniqueWork(WORK_MIDNIGHT)
    }

    private suspend fun scheduleReminder(policy: ExistingWorkPolicy) {
        val profile = profileRepository.getProfile()
        val config = reminderRepository.getConfig()
        if (profile == null || !config.enabled) {
            workManager.cancelUniqueWork(WORK_REMINDER)
            return
        }
        val interval = ReminderPlanner.intervalMinutes(
            wake = profile.wakeTime,
            sleep = profile.sleepTime,
            goalMl = goalRepository.getTodayGoal(),
            cupMl = cupRepository.getSelectedCupMl(),
            overrideMinutes = config.intervalMinutes
        )
        val next = ReminderPlanner.nextReminder(time.now(), profile.wakeTime, profile.sleepTime, interval)
        enqueue(WORK_REMINDER, ReminderWorker::class.java, delayUntil(next), policy)
    }

    private suspend fun scheduleStreakCheck(policy: ExistingWorkPolicy) {
        val profile = profileRepository.getProfile()
        if (profile == null || !reminderRepository.getConfig().streakNotify) {
            workManager.cancelUniqueWork(WORK_STREAK_CHECK)
            return
        }
        val next = ReminderPlanner.nextStreakCheck(time.now(), profile.sleepTime)
        enqueue(WORK_STREAK_CHECK, StreakCheckWorker::class.java, delayUntil(next), policy)
    }

    private fun scheduleMidnight(policy: ExistingWorkPolicy) {
        val next = ReminderPlanner.nextMidnight(time.now()).plusMinutes(MIDNIGHT_MARGIN_MINUTES)
        enqueue(WORK_MIDNIGHT, MidnightWorker::class.java, delayUntil(next), policy)
    }

    private fun delayUntil(dateTime: LocalDateTime): Long =
        (time.millisOf(dateTime) - time.currentMillis()).coerceAtLeast(0)

    private fun enqueue(
        name: String,
        workerClass: Class<out ListenableWorker>,
        delayMillis: Long,
        policy: ExistingWorkPolicy
    ) {
        val request = OneTimeWorkRequest.Builder(workerClass)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniqueWork(name, policy, request)
    }

    private companion object {
        const val WORK_REMINDER = "work_reminder"
        const val WORK_STREAK_CHECK = "work_streak_check"
        const val WORK_MIDNIGHT = "work_midnight"
        const val MIDNIGHT_MARGIN_MINUTES = 1L
    }
}
