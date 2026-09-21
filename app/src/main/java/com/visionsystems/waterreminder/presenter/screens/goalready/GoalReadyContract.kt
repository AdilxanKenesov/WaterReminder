package com.visionsystems.waterreminder.presenter.screens.goalready

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import org.orbitmvi.orbit.OrbitContainerHost

interface GoalReadyContract {

    interface GoalReadyViewModel : OrbitContainerHost<GoalReadyUiState, GoalReadyUiState, SideEffect> {
        fun onEventDispatcher(event: GoalReadyEvent)
    }

    sealed interface GoalReadyEvent {
        data object StartClicked : GoalReadyEvent
        data object AdjustClicked : GoalReadyEvent
    }

    data class GoalReadyUiState(
        val isLoading: Boolean = true,
        val goalMl: Int = 0,
        val weightKg: Int = 0,
        val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
        val cupMl: Int = 250
    ) {
        val glasses: Int get() = if (cupMl <= 0) 0 else (goalMl + cupMl - 1) / cupMl
    }

    sealed interface SideEffect

    interface Direction {
        fun openMain()
        fun openSetGoal()
    }
}
