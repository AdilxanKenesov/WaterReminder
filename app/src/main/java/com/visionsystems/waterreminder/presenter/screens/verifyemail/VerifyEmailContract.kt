package com.visionsystems.waterreminder.presenter.screens.verifyemail

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import org.orbitmvi.orbit.OrbitContainerHost

interface VerifyEmailContract {

    interface VerifyEmailViewModel : OrbitContainerHost<VerifyEmailUiState, VerifyEmailUiState, SideEffect> {
        fun onEventDispatcher(event: VerifyEmailEvent)
    }

    sealed interface VerifyEmailEvent {
        data object VerifiedClicked : VerifyEmailEvent
        data object OpenEmailClicked : VerifyEmailEvent
        data object ResendClicked : VerifyEmailEvent
        data object ScreenResumed : VerifyEmailEvent
        data object BackClicked : VerifyEmailEvent
    }

    data class VerifyEmailUiState(
        val email: String = "",
        val isChecking: Boolean = false,
        val isResending: Boolean = false
    )

    sealed interface SideEffect {
        data object OpenEmailApp : SideEffect
        data class ShowMessage(val message: UiText) : SideEffect
    }

    interface Direction {
        fun openStart(start: AppStart)
        fun openSignIn()
    }
}
