package com.visionsystems.waterreminder.presenter.screens.verifyemail

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.usecase.verifyemail.VerifyEmailUseCase
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import com.visionsystems.waterreminder.presenter.ui.util.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val direction: VerifyEmailContract.Direction,
    private val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel(), VerifyEmailContract.VerifyEmailViewModel {

    override val container = orbitContainer<VerifyEmailContract.VerifyEmailUiState, VerifyEmailContract.SideEffect>(
        VerifyEmailContract.VerifyEmailUiState(email = verifyEmailUseCase.currentEmail())
    )

    init {
        intent {
            verifyEmailUseCase.observeOnline().collect { online -> reduce { state.copy(isOffline = !online) } }
        }
    }

    override fun onEventDispatcher(event: VerifyEmailContract.VerifyEmailEvent) {
        when (event) {
            VerifyEmailContract.VerifyEmailEvent.VerifiedClicked -> check(silent = false)

            VerifyEmailContract.VerifyEmailEvent.ScreenResumed -> check(silent = true)

            VerifyEmailContract.VerifyEmailEvent.OpenEmailClicked -> intent {
                postSideEffect(VerifyEmailContract.SideEffect.OpenEmailApp)
            }

            VerifyEmailContract.VerifyEmailEvent.ResendClicked -> intent {
                if (state.isResending) return@intent
                if (state.isOffline) {
                    postSideEffect(VerifyEmailContract.SideEffect.ShowMessage(UiText.Res(R.string.error_network)))
                    return@intent
                }
                reduce { state.copy(isResending = true) }
                verifyEmailUseCase.resendLink()
                    .onSuccess { postSideEffect(VerifyEmailContract.SideEffect.ShowMessage(UiText.Res(R.string.link_sent_again))) }
                    .onFailure { postSideEffect(VerifyEmailContract.SideEffect.ShowMessage(it.toUiText())) }
                reduce { state.copy(isResending = false) }
            }

            VerifyEmailContract.VerifyEmailEvent.BackClicked -> {
                verifyEmailUseCase.signOut()
                direction.openSignIn()
            }
        }
    }

    private fun check(silent: Boolean) = intent {
        if (state.isChecking) return@intent
        if (state.isOffline) {
            if (!silent) postSideEffect(VerifyEmailContract.SideEffect.ShowMessage(UiText.Res(R.string.error_network)))
            return@intent
        }
        reduce { state.copy(isChecking = !silent) }
        verifyEmailUseCase.checkVerified()
            .onSuccess { start ->
                when {
                    start != null -> direction.openStart(start)
                    !silent -> postSideEffect(VerifyEmailContract.SideEffect.ShowMessage(UiText.Res(R.string.not_verified_yet)))
                }
            }
            .onFailure { if (!silent) postSideEffect(VerifyEmailContract.SideEffect.ShowMessage(it.toUiText())) }
        reduce { state.copy(isChecking = false) }
    }
}
