package com.visionsystems.waterreminder.presenter.screens.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroSecondaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextField
import com.visionsystems.waterreminder.presenter.ui.components.OrDivider
import com.visionsystems.waterreminder.presenter.ui.components.ScreenTitle
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.GoogleSignInResult
import com.visionsystems.waterreminder.presenter.ui.util.asString
import com.visionsystems.waterreminder.presenter.ui.util.requestGoogleIdToken
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignInScreen() {
    val viewModel: SignInContract.SignInViewModel = hiltViewModel<SignInViewModel>()
    val uiState = viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            SignInContract.SideEffect.LaunchGoogleSignIn -> {
                val event = when (val result = context.requestGoogleIdToken()) {
                    is GoogleSignInResult.Success -> SignInContract.SignInEvent.GoogleTokenReceived(result.idToken)
                    GoogleSignInResult.Cancelled -> SignInContract.SignInEvent.GoogleFailed(null)
                    is GoogleSignInResult.Failure -> SignInContract.SignInEvent.GoogleFailed(result.message)
                }
                viewModel.onEventDispatcher(event)
            }

            is SignInContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message.asString(context))
        }
    }
    SignInContent(uiState = uiState.value, snackbarHostState = snackbarHostState, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun SignInContent(
    uiState: SignInContract.SignInUiState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (SignInContract.SignInEvent) -> Unit
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
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(HydroPrimary, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(R.drawable.ic_drop), contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            ScreenTitle(title = stringResource(R.string.signin_title))
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HydroTextField(
                    label = stringResource(R.string.field_email),
                    value = uiState.email,
                    onValueChange = { onEventDispatcher(SignInContract.SignInEvent.EmailChanged(it)) },
                    placeholder = stringResource(R.string.email_placeholder),
                    keyboardType = KeyboardType.Email,
                    error = uiState.emailError?.let { stringResource(it) },
                    enabled = !uiState.isBusy
                )
                HydroTextField(
                    label = stringResource(R.string.field_password),
                    value = uiState.password,
                    onValueChange = { onEventDispatcher(SignInContract.SignInEvent.PasswordChanged(it)) },
                    isPassword = true,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        focusManager.clearFocus()
                        onEventDispatcher(SignInContract.SignInEvent.SignInClicked)
                    },
                    error = uiState.passwordError?.let { stringResource(it) },
                    enabled = !uiState.isBusy
                )
                HydroTextButton(
                    text = stringResource(R.string.action_forgot_password),
                    onClick = { onEventDispatcher(SignInContract.SignInEvent.ForgotPasswordClicked) },
                    modifier = Modifier.align(Alignment.End),
                    enabled = !uiState.isBusy
                )
            }
            HydroPrimaryButton(
                text = stringResource(R.string.action_sign_in),
                loading = uiState.isLoading,
                enabled = !uiState.isGoogleLoading,
                onClick = {
                    focusManager.clearFocus()
                    onEventDispatcher(SignInContract.SignInEvent.SignInClicked)
                }
            )
            OrDivider()
            HydroSecondaryButton(
                text = stringResource(if (uiState.isGoogleLoading) R.string.google_connecting else R.string.action_continue_google),
                leadingIcon = R.drawable.ic_google,
                tintIcon = false,
                enabled = !uiState.isBusy,
                onClick = { onEventDispatcher(SignInContract.SignInEvent.GoogleClicked) }
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.signin_no_account), style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp, color = HydroInkMuted)
                HydroTextButton(
                    text = stringResource(R.string.action_sign_up),
                    onClick = { onEventDispatcher(SignInContract.SignInEvent.CreateAccountClicked) }
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignInPreview() {
    HydroTheme {
        SignInContent(
            uiState = SignInContract.SignInUiState(email = "alex@example.com"),
            snackbarHostState = remember { SnackbarHostState() },
            onEventDispatcher = {}
        )
    }
}
