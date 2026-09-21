package com.visionsystems.waterreminder.presenter.screens.addwater

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.addwater.AddWaterUseCase
import com.visionsystems.waterreminder.presenter.ui.components.AddWaterRange
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class AddWaterViewModel @Inject constructor(
    private val direction: AddWaterContract.Direction,
    private val addWaterUseCase: AddWaterUseCase
) : ViewModel(), AddWaterContract.AddWaterViewModel {

    override val container = orbitContainer<AddWaterContract.AddWaterUiState, AddWaterContract.SideEffect>(AddWaterContract.AddWaterUiState())

    init {
        intent {
            val defaults = addWaterUseCase.loadDefaults()
            reduce {
                state.copy(
                    amountMl = defaults.selectedCupMl.coerceIn(AddWaterRange),
                    presets = defaults.cups.map { it.amountMl }.sorted().take(PRESET_COUNT),
                    unit = defaults.unit
                )
            }
        }
    }

    override fun onEventDispatcher(event: AddWaterContract.AddWaterEvent) {
        when (event) {
            is AddWaterContract.AddWaterEvent.AmountChanged -> intent {
                reduce { state.copy(amountMl = event.amountMl.coerceIn(AddWaterRange)) }
            }

            AddWaterContract.AddWaterEvent.AddClicked -> intent {
                if (state.isSaving) return@intent
                reduce { state.copy(isSaving = true) }
                val result = addWaterUseCase.addDrink(state.amountMl)
                if (result.goalJustReached) direction.openGoalReached() else direction.close()
            }
        }
    }

    private companion object {
        const val PRESET_COUNT = 4
    }
}
