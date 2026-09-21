package com.visionsystems.waterreminder.presenter.screens.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.components.HydroBackButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroSecondaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextField
import com.visionsystems.waterreminder.presenter.ui.components.OrDivider
import com.visionsystems.waterreminder.presenter.ui.components.ScreenTitle
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.GoogleSignInResult
import com.visionsystems.waterreminder.presenter.ui.util.MIN_PASSWORD_LENGTH
import com.visionsystems.waterreminder.presenter.ui.util.asString
import com.visionsystems.waterreminder.presenter.ui.util.requestGoogleIdToken
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignUpScreen() {
    val viewModel: SignUpContract.SignUpViewModel = hiltViewModel<SignUpViewModel>()
    val uiState = viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            SignUpContract.SideEffect.LaunchGoogleSignIn -> {
                val event = when (val result = context.requestGoogleIdToken()) {
                    is GoogleSignInResult.Success -> SignUpContract.SignUpEvent.GoogleTokenReceived(result.idToken)
                    GoogleSignInResult.Cancelled -> SignUpContract.SignUpEvent.GoogleFailed(null)
                    is GoogleSignInResult.Failure -> SignUpContract.SignUpEvent.GoogleFailed(result.message)
                }
                viewModel.onEventDispatcher(event)
            }

            is SignUpContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message.asString(context))
        }
    }
    SignUpContent(uiState = uiState.value, snackbarHostState = snackbarHostState, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun SignUpContent(
    uiState: SignUpContract.SignUpUiState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (SignUpContract.SignUpEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        containerColor = HydroBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HydroBackButton(onClick = { onEventDispatcher(SignUpContract.SignUpEvent.BackClicked) })
            ScreenTitle(title = stringResource(R.string.signup_title))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HydroTextField(
                    label = stringResource(R.string.field_name),
                    value = uiState.fullName,
                    onValueChange = { onEventDispatcher(SignUpContract.SignUpEvent.NameChanged(it)) },
                    placeholder = stringResource(R.string.name_placeholder),
                    error = uiState.nameError?.let { stringResource(it) },
                    enabled = !uiState.isBusy
                )
                HydroTextField(
                    label = stringResource(R.string.field_email),
                    value = uiState.email,
                    onValueChange = { onEventDispatcher(SignUpContract.SignUpEvent.EmailChanged(it)) },
                    placeholder = stringResource(R.string.email_placeholder),
                    keyboardType = KeyboardType.Email,
                    error = uiState.emailError?.let { stringResource(it) },
                    enabled = !uiState.isBusy
                )
                val passwordHint = pluralStringResource(R.plurals.password_hint, MIN_PASSWORD_LENGTH, MIN_PASSWORD_LENGTH)
                HydroTextField(
                    label = stringResource(R.string.field_password),
                    value = uiState.password,
                    onValueChange = { onEventDispatcher(SignUpContract.SignUpEvent.PasswordChanged(it)) },
                    isPassword = true,
                    imeAction = ImeAction.Done,
                    onImeAction = { focusManager.clearFocus() },
                    hint = if (uiState.passwordError) null else passwordHint,
                    error = if (uiState.passwordError) passwordHint else null,
                    enabled = !uiState.isBusy
                )
            }
            HydroPrimaryButton(
                text = stringResource(R.string.action_create_account),
                loading = uiState.isLoading,
                enabled = !uiState.isGoogleLoading,
                onClick = {
                    focusManager.clearFocus()
                    onEventDispatcher(SignUpContract.SignUpEvent.CreateClicked)
                }
            )
            OrDivider()
            HydroSecondaryButton(
                text = stringResource(if (uiState.isGoogleLoading) R.string.google_connecting else R.string.action_continue_google),
                leadingIcon = R.drawable.ic_google,
                tintIcon = false,
                enabled = !uiState.isBusy,
                onClick = { onEventDispatcher(SignUpContract.SignUpEvent.GoogleClicked) }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignUpPreview() {
    HydroTheme {
        SignUpContent(
            uiState = SignUpContract.SignUpUiState(fullName = "Alex"),
            snackbarHostState = remember { SnackbarHostState() },
            onEventDispatcher = {}
        )
    }
}
