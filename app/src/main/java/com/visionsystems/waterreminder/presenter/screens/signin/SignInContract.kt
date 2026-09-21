package com.visionsystems.waterreminder.presenter.screens.signin

import androidx.annotation.StringRes
import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import org.orbitmvi.orbit.OrbitContainerHost

interface SignInContract {

    interface SignInViewModel : OrbitContainerHost<SignInUiState, SignInUiState, SideEffect> {
        fun onEventDispatcher(event: SignInEvent)
    }

    sealed interface SignInEvent {
        data class EmailChanged(val value: String) : SignInEvent
        data class PasswordChanged(val value: String) : SignInEvent
        data object SignInClicked : SignInEvent
        data object ForgotPasswordClicked : SignInEvent
        data object GoogleClicked : SignInEvent
        data class GoogleTokenReceived(val idToken: String) : SignInEvent
        data class GoogleFailed(val message: UiText?) : SignInEvent
        data object CreateAccountClicked : SignInEvent
    }

    data class SignInUiState(
        val email: String = "",
        val password: String = "",
        @param:StringRes val emailError: Int? = null,
        @param:StringRes val passwordError: Int? = null,
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
        fun openSignUp()
        fun openStart(start: AppStart)
    }
}
