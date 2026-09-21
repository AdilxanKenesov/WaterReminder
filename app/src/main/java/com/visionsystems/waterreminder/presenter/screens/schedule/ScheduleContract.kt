package com.visionsystems.waterreminder.presenter.screens.schedule

import org.orbitmvi.orbit.OrbitContainerHost
import java.time.LocalTime

interface ScheduleContract {

    interface ScheduleViewModel : OrbitContainerHost<ScheduleUiState, ScheduleUiState, SideEffect> {
        fun onEventDispatcher(event: ScheduleEvent)
    }

    enum class TimeTarget {
        WAKE,
        SLEEP
    }

    sealed interface ScheduleEvent {
        data class PickerOpened(val target: TimeTarget) : ScheduleEvent
        data class TimePicked(val time: LocalTime) : ScheduleEvent
        data object PickerDismissed : ScheduleEvent
        data object ContinueClicked : ScheduleEvent
        data object BackClicked : ScheduleEvent
    }

    data class ScheduleUiState(
        val wakeTime: LocalTime = LocalTime.of(7, 0),
        val sleepTime: LocalTime = LocalTime.of(23, 0),
        val intervalMinutes: Int = 120,
        val slots: List<LocalTime> = emptyList(),
        val picker: TimeTarget? = null,
        val isSaving: Boolean = false
    )

    sealed interface SideEffect

    interface Direction {
        fun openGoalReady()
        fun back()
    }
}
