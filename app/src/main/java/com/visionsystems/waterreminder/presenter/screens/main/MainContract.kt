package com.visionsystems.waterreminder.presenter.screens.main

import com.visionsystems.waterreminder.navigation.MainTab
import org.orbitmvi.orbit.OrbitContainerHost

interface MainContract {

    interface MainViewModel : OrbitContainerHost<MainUiState, MainUiState, SideEffect> {
        fun onEventDispatcher(event: MainEvent)
    }

    sealed interface MainEvent {
        data class TabSelected(val tab: MainTab) : MainEvent
        data object AddWaterClicked : MainEvent
    }

    data class MainUiState(
        val isReady: Boolean = true
    )

    sealed interface SideEffect

    interface Direction {
        fun selectTab(tab: MainTab)
        fun openAddWater()
    }
}
