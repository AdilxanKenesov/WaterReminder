package com.visionsystems.waterreminder.domain.util

import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.domain.module.ReminderConfigUiData
import com.visionsystems.waterreminder.domain.module.ReminderSlotState
import com.visionsystems.waterreminder.domain.module.ReminderSlotUiData
import com.visionsystems.waterreminder.domain.module.UserProfileUiData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.LocalTime

object ReminderPreview {

    val DEFAULT_WAKE: LocalTime = LocalTime.of(7, 0)
    val DEFAULT_SLEEP: LocalTime = LocalTime.of(23, 0)
    const val DEFAULT_CUP_ML = 250
    private const val TICK_MS = 60_000L

    fun selectedCupMl(cups: List<CupSizeUiData>): Int = cups.firstOrNull { it.isSelected }?.amountMl ?: DEFAULT_CUP_ML

    fun interval(profile: UserProfileUiData?, goalMl: Int, cupMl: Int, config: ReminderConfigUiData): Int =
        ReminderPlanner.intervalMinutes(
            wake = profile?.wakeTime ?: DEFAULT_WAKE,
            sleep = profile?.sleepTime ?: DEFAULT_SLEEP,
            goalMl = goalMl,
            cupMl = cupMl,
            overrideMinutes = config.intervalMinutes
        )

    fun next(
        now: LocalDateTime,
        profile: UserProfileUiData?,
        intervalMinutes: Int,
        config: ReminderConfigUiData,
        goalCompleted: Boolean
    ): LocalTime? {
        if (!config.enabled || goalCompleted || profile == null) return null
        return ReminderPlanner.nextReminder(now, profile.wakeTime, profile.sleepTime, intervalMinutes).toLocalTime()
    }

    fun todaySlots(now: LocalDateTime, profile: UserProfileUiData?, intervalMinutes: Int, next: LocalTime?): List<ReminderSlotUiData> {
        val wake = profile?.wakeTime ?: DEFAULT_WAKE
        val sleep = profile?.sleepTime ?: DEFAULT_SLEEP
        val current = now.toLocalTime()
        return ReminderPlanner.slots(wake, sleep, intervalMinutes).map { slot ->
            val state = when {
                slot == next -> ReminderSlotState.NEXT
                slot.isBefore(current) -> ReminderSlotState.DONE
                else -> ReminderSlotState.LATER
            }
            ReminderSlotUiData(slot, state)
        }
    }

    fun minuteTicker(): Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(TICK_MS)
        }
    }
}
