package com.visionsystems.waterreminder.presenter.screens.setgoal

import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.domain.module.WaterUnit
import org.orbitmvi.orbit.OrbitContainerHost

interface SetGoalContract {

    interface SetGoalViewModel : OrbitContainerHost<SetGoalUiState, SetGoalUiState, SideEffect> {
        fun onEventDispatcher(event: SetGoalEvent)
    }

    sealed interface SetGoalEvent {
        data object IncreaseClicked : SetGoalEvent
        data object DecreaseClicked : SetGoalEvent
        data object UseRecommendedClicked : SetGoalEvent
        data class CupSelected(val cupId: Long) : SetGoalEvent
        data object SaveClicked : SetGoalEvent
        data object BackClicked : SetGoalEvent
    }

    data class SetGoalUiState(
        val isLoading: Boolean = true,
        val goalMl: Int = 2000,
        val recommendedMl: Int = 2000,
        val minGoalMl: Int = 1000,
        val maxGoalMl: Int = 5000,
        val cups: List<CupSizeUiData> = emptyList(),
        val selectedCupId: Long? = null,
        val unit: WaterUnit = WaterUnit.ML,
        val isSaving: Boolean = false
    )

    sealed interface SideEffect

    interface Direction {
        fun back()
    }

    companion object {
        const val GOAL_STEP_ML = 50
    }
}
