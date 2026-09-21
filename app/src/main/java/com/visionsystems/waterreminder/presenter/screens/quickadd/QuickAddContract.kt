package com.visionsystems.waterreminder.presenter.screens.quickadd

import com.visionsystems.waterreminder.domain.module.WaterUnit
import org.orbitmvi.orbit.OrbitContainerHost

interface QuickAddContract {

    interface QuickAddViewModel : OrbitContainerHost<QuickAddUiState, QuickAddUiState, SideEffect> {
        fun onEventDispatcher(event: QuickAddEvent)
    }

    sealed interface QuickAddEvent {
        data class AmountChanged(val amountMl: Int) : QuickAddEvent
        data object AddClicked : QuickAddEvent
        data object DismissClicked : QuickAddEvent
    }

    data class QuickAddUiState(
        val isLoading: Boolean = true,
        val amountMl: Int = 250,
        val presets: List<Int> = emptyList(),
        val unit: WaterUnit = WaterUnit.ML,
        val isSaving: Boolean = false
    )

    sealed interface SideEffect {
        data object Close : SideEffect
        data object OpenApp : SideEffect
    }

    interface Direction
}
