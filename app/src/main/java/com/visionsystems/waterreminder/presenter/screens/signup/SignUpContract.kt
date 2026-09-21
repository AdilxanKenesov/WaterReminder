package com.visionsystems.waterreminder.presenter.screens.signup

import androidx.annotation.StringRes
import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import org.orbitmvi.orbit.OrbitContainerHost

interface SignUpContract {

    interface SignUpViewModel : OrbitContainerHost<SignUpUiState, SignUpUiState, SideEffect> {
        fun onEventDispatcher(event: SignUpEvent)
    }

    sealed interface SignUpEvent {
        data class NameChanged(val value: String) : SignUpEvent
        data class EmailChanged(val value: String) : SignUpEvent
        data class PasswordChanged(val value: String) : SignUpEvent
        data object CreateClicked : SignUpEvent
        data object GoogleClicked : SignUpEvent
        data class GoogleTokenReceived(val idToken: String) : SignUpEvent
        data class GoogleFailed(val message: UiText?) : SignUpEvent
        data object BackClicked : SignUpEvent
    }

    data class SignUpUiState(
        val fullName: String = "",
        val email: String = "",
        val password: String = "",
        @param:StringRes val nameError: Int? = null,
        @param:StringRes val emailError: Int? = null,
        val passwordError: Boolean = false,
        val isLoading: Boolean = false,
        val isGoogleLoading: Boolean = false
    ) {
        val isBusy: Boolean get() = isLoading || isGoogleLoading
    }

    sealed interface SideEffect {
        data object LaunchGoogleSignIn : SideEffect
        data class ShowMessage(val message: UiText) : SideEffect
    }

    interface Direction {
        fun openVerifyEmail()
        fun openStart(start: AppStart)
        fun back()
    }
}
