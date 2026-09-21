package com.visionsystems.waterreminder.presenter.screens.reminders

import com.visionsystems.waterreminder.domain.module.ReminderConfigUiData
import com.visionsystems.waterreminder.domain.module.ReminderSlotUiData
import org.orbitmvi.orbit.OrbitContainerHost
import java.time.LocalTime

interface RemindersContract {

    interface RemindersViewModel : OrbitContainerHost<RemindersUiState, RemindersUiState, SideEffect> {
        fun onEventDispatcher(event: RemindersEvent)
    }

    enum class TimeTarget {
        WAKE,
        SLEEP
    }

    sealed interface RemindersEvent {
        data class EnabledChanged(val enabled: Boolean) : RemindersEvent
        data class FrequencySelected(val intervalMinutes: Int?) : RemindersEvent
        data class GoalNotifyChanged(val enabled: Boolean) : RemindersEvent
        data class StreakNotifyChanged(val enabled: Boolean) : RemindersEvent
        data class AchievementNotifyChanged(val enabled: Boolean) : RemindersEvent
        data class SoundChanged(val enabled: Boolean) : RemindersEvent
        data class PickerOpened(val target: TimeTarget) : RemindersEvent
        data class TimePicked(val time: LocalTime) : RemindersEvent
        data object PickerDismissed : RemindersEvent
        data object EnableNotificationsClicked : RemindersEvent
    }

    data class RemindersUiState(
        val isLoading: Boolean = true,
        val config: ReminderConfigUiData = ReminderConfigUiData(),
        val wakeTime: LocalTime = LocalTime.of(7, 0),
        val sleepTime: LocalTime = LocalTime.of(23, 0),
        val nextReminder: LocalTime? = null,
        val slots: List<ReminderSlotUiData> = emptyList(),
        val intervalMinutes: Int = 120,
        val goalCompleted: Boolean = false,
        val canNotify: Boolean = true,
        val picker: TimeTarget? = null
    )

    sealed interface SideEffect {
        data object OpenNotificationSettings : SideEffect
    }

    interface Direction

    companion object {
        val FREQUENCIES: List<Int?> = listOf(null, 60, 120, 180)
    }
}
