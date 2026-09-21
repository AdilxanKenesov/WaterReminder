package com.visionsystems.waterreminder.presenter.screens.addwater

import com.visionsystems.waterreminder.domain.module.WaterUnit
import org.orbitmvi.orbit.OrbitContainerHost

interface AddWaterContract {

    interface AddWaterViewModel : OrbitContainerHost<AddWaterUiState, AddWaterUiState, SideEffect> {
        fun onEventDispatcher(event: AddWaterEvent)
    }

    sealed interface AddWaterEvent {
        data class AmountChanged(val amountMl: Int) : AddWaterEvent
        data object AddClicked : AddWaterEvent
    }

    data class AddWaterUiState(
        val amountMl: Int = 250,
        val presets: List<Int> = emptyList(),
        val unit: WaterUnit = WaterUnit.ML,
        val isSaving: Boolean = false
    )

    sealed interface SideEffect

    interface Direction {
        fun close()
        fun openGoalReached()
    }
}
