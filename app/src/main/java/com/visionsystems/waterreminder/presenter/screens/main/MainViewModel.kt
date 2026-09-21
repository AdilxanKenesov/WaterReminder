package com.visionsystems.waterreminder.presenter.screens.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val direction: MainContract.Direction
) : ViewModel(), MainContract.MainViewModel {

    override val container = orbitContainer<MainContract.MainUiState, MainContract.SideEffect>(MainContract.MainUiState())

    override fun onEventDispatcher(event: MainContract.MainEvent) {
        when (event) {
            is MainContract.MainEvent.TabSelected -> direction.selectTab(event.tab)
            MainContract.MainEvent.AddWaterClicked -> direction.openAddWater()
        }
    }
}
