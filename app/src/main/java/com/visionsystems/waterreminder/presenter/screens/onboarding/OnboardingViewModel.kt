package com.visionsystems.waterreminder.presenter.screens.onboarding

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.onboarding.OnboardingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val direction: OnboardingContract.Direction,
    private val onboardingUseCase: OnboardingUseCase
) : ViewModel(), OnboardingContract.OnboardingViewModel {

    override val container = orbitContainer<OnboardingContract.OnboardingUiState, OnboardingContract.SideEffect>(
        OnboardingContract.OnboardingUiState()
    )

    override fun onEventDispatcher(event: OnboardingContract.OnboardingEvent) {
        when (event) {
            OnboardingContract.OnboardingEvent.NextClicked -> intent {
                if (state.isLastPage) finish() else reduce { state.copy(page = state.page + 1) }
            }

            OnboardingContract.OnboardingEvent.SkipClicked -> finish()

            is OnboardingContract.OnboardingEvent.PageChanged -> intent {
                reduce { state.copy(page = event.page) }
            }
        }
    }

    private fun finish() {
        onboardingUseCase.completeOnboarding()
        direction.openSignIn()
    }
}
