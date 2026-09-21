package com.visionsystems.waterreminder.presenter.screens.onboarding

import org.orbitmvi.orbit.OrbitContainerHost

interface OnboardingContract {

    interface OnboardingViewModel : OrbitContainerHost<OnboardingUiState, OnboardingUiState, SideEffect> {
        fun onEventDispatcher(event: OnboardingEvent)
    }

    sealed interface OnboardingEvent {
        data object NextClicked : OnboardingEvent
        data object SkipClicked : OnboardingEvent
        data class PageChanged(val page: Int) : OnboardingEvent
    }

    data class OnboardingUiState(
        val page: Int = 0,
        val pageCount: Int = OnboardingContract.PAGE_COUNT
    ) {
        val isLastPage: Boolean get() = page == pageCount - 1
    }

    sealed interface SideEffect

    interface Direction {
        fun openSignIn()
    }

    companion object {
        const val PAGE_COUNT = 3
    }
}
