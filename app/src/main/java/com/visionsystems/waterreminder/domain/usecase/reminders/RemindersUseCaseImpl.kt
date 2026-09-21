package com.visionsystems.waterreminder.domain.usecase.reminders

import com.visionsystems.waterreminder.domain.module.ReminderConfigUiData
import com.visionsystems.waterreminder.domain.module.RemindersUiData
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.NotificationRepository
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.util.ReminderPreview
import com.visionsystems.waterreminder.domain.util.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalTime
import javax.inject.Inject

class RemindersUseCaseImpl @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val profileRepository: ProfileRepository,
    private val drinkRepository: DrinkRepository,
    private val cupRepository: CupRepository,
    private val notificationRepository: NotificationRepository,
    private val reminderScheduler: ReminderScheduler,
    private val time: TimeProvider
) : RemindersUseCase {

    override fun observeReminders(): Flow<RemindersUiData> =
        combine(
            reminderRepository.observeConfig(),
            profileRepository.observeProfile(),
            drinkRepository.observeToday(),
            cupRepository.observeCups(),
            ReminderPreview.minuteTicker()
        ) { config, profile, progress, cups, _ ->
            val now = time.now()
            val interval = ReminderPreview.interval(profile, progress.goalMl, ReminderPreview.selectedCupMl(cups), config)
            val next = ReminderPreview.next(now, profile, interval, config, progress.isCompleted)
            RemindersUiData(
                config = config,
                wakeTime = profile?.wakeTime ?: ReminderPreview.DEFAULT_WAKE,
                sleepTime = profile?.sleepTime ?: ReminderPreview.DEFAULT_SLEEP,
                nextReminder = next,
                slots = ReminderPreview.todaySlots(now, profile, interval, next),
                intervalMinutes = interval,
                goalCompleted = progress.isCompleted,
                canNotify = notificationRepository.canNotify()
            )
        }

    override suspend fun updateConfig(config: ReminderConfigUiData) {
        reminderRepository.updateConfig(config)
        reminderScheduler.rescheduleAll()
    }

    override suspend fun updateTimes(wake: LocalTime, sleep: LocalTime) {
        val profile = profileRepository.getProfile() ?: return
        profileRepository.saveProfile(profile.copy(wakeTime = wake, sleepTime = sleep))
        reminderScheduler.rescheduleAll()
    }
}
