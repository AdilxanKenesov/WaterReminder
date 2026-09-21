package com.visionsystems.waterreminder.presenter.screens.signin

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.usecase.signin.SignInUseCase
import com.visionsystems.waterreminder.presenter.ui.util.isValidEmail
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import com.visionsystems.waterreminder.presenter.ui.util.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val direction: SignInContract.Direction,
    private val signInUseCase: SignInUseCase
) : ViewModel(), SignInContract.SignInViewModel {

    override val container = orbitContainer<SignInContract.SignInUiState, SignInContract.SideEffect>(SignInContract.SignInUiState())

    override fun onEventDispatcher(event: SignInContract.SignInEvent) {
        when (event) {
            is SignInContract.SignInEvent.EmailChanged -> intent {
                reduce { state.copy(email = event.value, emailError = null) }
            }

            is SignInContract.SignInEvent.PasswordChanged -> intent {
                reduce { state.copy(password = event.value, passwordError = null) }
            }

            SignInContract.SignInEvent.SignInClicked -> signIn()

            SignInContract.SignInEvent.ForgotPasswordClicked -> intent {
                if (!state.email.isValidEmail()) {
                    reduce { state.copy(emailError = R.string.error_email_for_reset) }
                    return@intent
                }
                signInUseCase.sendPasswordReset(state.email)
                    .onSuccess { postSideEffect(SignInContract.SideEffect.ShowMessage(UiText.Res(R.string.reset_link_sent, listOf(state.email.trim())))) }
                    .onFailure { postSideEffect(SignInContract.SideEffect.ShowMessage(it.toUiText())) }
            }

            SignInContract.SignInEvent.GoogleClicked -> intent {
                if (state.isBusy) return@intent
                reduce { state.copy(isGoogleLoading = true) }
                postSideEffect(SignInContract.SideEffect.LaunchGoogleSignIn)
            }

            is SignInContract.SignInEvent.GoogleTokenReceived -> intent {
                signInUseCase.signInWithGoogle(event.idToken)
                    .onSuccess { direction.openStart(it) }
                    .onFailure { postSideEffect(SignInContract.SideEffect.ShowMessage(it.toUiText())) }
                reduce { state.copy(isGoogleLoading = false) }
            }

            is SignInContract.SignInEvent.GoogleFailed -> intent {
                reduce { state.copy(isGoogleLoading = false) }
                event.message?.let { postSideEffect(SignInContract.SideEffect.ShowMessage(it)) }
            }

            SignInContract.SignInEvent.CreateAccountClicked -> direction.openSignUp()
        }
    }

    private fun signIn() = intent {
        if (state.isBusy) return@intent
        val emailError = if (state.email.isValidEmail()) null else R.string.error_email_invalid
        val passwordError = if (state.password.isEmpty()) R.string.error_password_empty else null
        if (emailError != null || passwordError != null) {
            reduce { state.copy(emailError = emailError, passwordError = passwordError) }
            return@intent
        }
        reduce { state.copy(isLoading = true) }
        signInUseCase.signIn(state.email, state.password)
            .onSuccess { direction.openStart(it) }
            .onFailure { postSideEffect(SignInContract.SideEffect.ShowMessage(it.toUiText())) }
        reduce { state.copy(isLoading = false) }
    }
}
