package com.visionsystems.waterreminder.presenter.screens.goalreached

import com.visionsystems.waterreminder.domain.module.WaterUnit
import org.orbitmvi.orbit.OrbitContainerHost

interface GoalReachedContract {

    interface GoalReachedViewModel : OrbitContainerHost<GoalReachedUiState, GoalReachedUiState, SideEffect> {
        fun onEventDispatcher(event: GoalReachedEvent)
    }

    sealed interface GoalReachedEvent {
        data object BackHomeClicked : GoalReachedEvent
    }

    data class GoalReachedUiState(
        val isLoading: Boolean = true,
        val consumedMl: Int = 0,
        val streak: Int = 0,
        val unit: WaterUnit = WaterUnit.ML
    )

    sealed interface SideEffect

    interface Direction {
        fun back()
    }
}
