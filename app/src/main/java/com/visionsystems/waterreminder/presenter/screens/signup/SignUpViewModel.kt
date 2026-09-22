package com.visionsystems.waterreminder.presenter.screens.signup

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.usecase.signup.SignUpUseCase
import com.visionsystems.waterreminder.presenter.ui.util.MIN_PASSWORD_LENGTH
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import com.visionsystems.waterreminder.presenter.ui.util.isValidEmail
import com.visionsystems.waterreminder.presenter.ui.util.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val direction: SignUpContract.Direction,
    private val signUpUseCase: SignUpUseCase
) : ViewModel(), SignUpContract.SignUpViewModel {

    override val container = orbitContainer<SignUpContract.SignUpUiState, SignUpContract.SideEffect>(SignUpContract.SignUpUiState())

    init {
        intent {
            signUpUseCase.observeOnline().collect { online -> reduce { state.copy(isOffline = !online) } }
        }
    }

    override fun onEventDispatcher(event: SignUpContract.SignUpEvent) {
        when (event) {
            is SignUpContract.SignUpEvent.NameChanged -> intent {
                reduce { state.copy(fullName = event.value, nameError = null) }
            }

            is SignUpContract.SignUpEvent.EmailChanged -> intent {
                reduce { state.copy(email = event.value, emailError = null) }
            }

            is SignUpContract.SignUpEvent.PasswordChanged -> intent {
                reduce { state.copy(password = event.value, passwordError = false) }
            }

            SignUpContract.SignUpEvent.CreateClicked -> createAccount()

            SignUpContract.SignUpEvent.GoogleClicked -> intent {
                if (state.isBusy) return@intent
                if (state.isOffline) {
                    postSideEffect(SignUpContract.SideEffect.ShowMessage(UiText.Res(R.string.error_network)))
                    return@intent
                }
                reduce { state.copy(isGoogleLoading = true) }
                postSideEffect(SignUpContract.SideEffect.LaunchGoogleSignIn)
            }

            is SignUpContract.SignUpEvent.GoogleTokenReceived -> intent {
                signUpUseCase.signInWithGoogle(event.idToken)
                    .onSuccess { direction.openStart(it) }
                    .onFailure { postSideEffect(SignUpContract.SideEffect.ShowMessage(it.toUiText())) }
                reduce { state.copy(isGoogleLoading = false) }
            }

            is SignUpContract.SignUpEvent.GoogleFailed -> intent {
                reduce { state.copy(isGoogleLoading = false) }
                event.message?.let { postSideEffect(SignUpContract.SideEffect.ShowMessage(it)) }
            }

            SignUpContract.SignUpEvent.BackClicked -> direction.back()
        }
    }

    private fun createAccount() = intent {
        if (state.isBusy) return@intent
        if (state.isOffline) {
            postSideEffect(SignUpContract.SideEffect.ShowMessage(UiText.Res(R.string.error_network)))
            return@intent
        }
        val nameError = if (state.fullName.isBlank()) R.string.error_name_empty else null
        val emailError = if (state.email.isValidEmail()) null else R.string.error_email_invalid
        val passwordError = state.password.length < MIN_PASSWORD_LENGTH
        if (nameError != null || emailError != null || passwordError) {
            reduce { state.copy(nameError = nameError, emailError = emailError, passwordError = passwordError) }
            return@intent
        }
        reduce { state.copy(isLoading = true) }
        signUpUseCase.signUp(state.fullName, state.email, state.password)
            .onSuccess { direction.openVerifyEmail() }
            .onFailure { postSideEffect(SignUpContract.SideEffect.ShowMessage(it.toUiText())) }
        reduce { state.copy(isLoading = false) }
    }
}
