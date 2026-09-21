package com.visionsystems.waterreminder.domain.module

import java.time.LocalTime

enum class ReminderSlotState {
    DONE,
    NEXT,
    LATER
}

data class ReminderSlotUiData(
    val time: LocalTime,
    val state: ReminderSlotState
)

data class RemindersUiData(
    val config: ReminderConfigUiData,
    val wakeTime: LocalTime,
    val sleepTime: LocalTime,
    val nextReminder: LocalTime?,
    val slots: List<ReminderSlotUiData>,
    val intervalMinutes: Int,
    val goalCompleted: Boolean,
    val canNotify: Boolean
)
