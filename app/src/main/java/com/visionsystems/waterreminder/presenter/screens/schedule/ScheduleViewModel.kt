package com.visionsystems.waterreminder.presenter.screens.schedule

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.schedule.ScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val direction: ScheduleContract.Direction,
    private val scheduleUseCase: ScheduleUseCase
) : ViewModel(), ScheduleContract.ScheduleViewModel {

    override val container = orbitContainer<ScheduleContract.ScheduleUiState, ScheduleContract.SideEffect>(ScheduleContract.ScheduleUiState())

    init {
        intent {
            scheduleUseCase.loadProfile()?.let { profile ->
                reduce { state.copy(wakeTime = profile.wakeTime, sleepTime = profile.sleepTime) }
            }
            refreshPlan()
        }
    }

    override fun onEventDispatcher(event: ScheduleContract.ScheduleEvent) {
        when (event) {
            is ScheduleContract.ScheduleEvent.PickerOpened -> intent { reduce { state.copy(picker = event.target) } }

            ScheduleContract.ScheduleEvent.PickerDismissed -> intent { reduce { state.copy(picker = null) } }

            is ScheduleContract.ScheduleEvent.TimePicked -> intent {
                reduce {
                    when (state.picker) {
                        ScheduleContract.TimeTarget.WAKE -> state.copy(wakeTime = event.time, picker = null)
                        ScheduleContract.TimeTarget.SLEEP -> state.copy(sleepTime = event.time, picker = null)
                        null -> state
                    }
                }
                refreshPlan()
            }

            ScheduleContract.ScheduleEvent.ContinueClicked -> intent {
                if (state.isSaving) return@intent
                reduce { state.copy(isSaving = true) }
                scheduleUseCase.saveTimes(state.wakeTime, state.sleepTime)
                reduce { state.copy(isSaving = false) }
                direction.openGoalReady()
            }

            ScheduleContract.ScheduleEvent.BackClicked -> direction.back()
        }
    }

    private fun refreshPlan() = intent {
        val plan = scheduleUseCase.previewPlan(state.wakeTime, state.sleepTime)
        reduce { state.copy(intervalMinutes = plan.intervalMinutes, slots = plan.slots) }
    }
}
