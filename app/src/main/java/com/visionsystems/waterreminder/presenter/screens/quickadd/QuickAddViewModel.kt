package com.visionsystems.waterreminder.presenter.screens.quickadd

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.quickadd.QuickAddUseCase
import com.visionsystems.waterreminder.presenter.ui.components.AddWaterRange
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class QuickAddViewModel @Inject constructor(
    private val direction: QuickAddContract.Direction,
    private val quickAddUseCase: QuickAddUseCase
) : ViewModel(), QuickAddContract.QuickAddViewModel {

    override val container = orbitContainer<QuickAddContract.QuickAddUiState, QuickAddContract.SideEffect>(QuickAddContract.QuickAddUiState())

    init {
        intent {
            if (!quickAddUseCase.isReady()) {
                postSideEffect(QuickAddContract.SideEffect.OpenApp)
                return@intent
            }
            val defaults = quickAddUseCase.loadDefaults()
            reduce {
                state.copy(
                    isLoading = false,
                    amountMl = defaults.selectedCupMl.coerceIn(AddWaterRange),
                    presets = defaults.cups.map { it.amountMl }.sorted().take(PRESET_COUNT),
                    unit = defaults.unit
                )
            }
        }
    }

    override fun onEventDispatcher(event: QuickAddContract.QuickAddEvent) {
        when (event) {
            is QuickAddContract.QuickAddEvent.AmountChanged -> intent {
                reduce { state.copy(amountMl = event.amountMl.coerceIn(AddWaterRange)) }
            }

            QuickAddContract.QuickAddEvent.AddClicked -> intent {
                if (state.isSaving || state.isLoading) return@intent
                reduce { state.copy(isSaving = true) }
                quickAddUseCase.addDrink(state.amountMl)
                postSideEffect(QuickAddContract.SideEffect.Close)
            }

            QuickAddContract.QuickAddEvent.DismissClicked -> intent {
                if (!state.isSaving) postSideEffect(QuickAddContract.SideEffect.Close)
            }
        }
    }

    private companion object {
        const val PRESET_COUNT = 4
    }
}
