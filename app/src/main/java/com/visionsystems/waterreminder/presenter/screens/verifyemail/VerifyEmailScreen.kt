package com.visionsystems.waterreminder.presenter.screens.verifyemail

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.components.HydroBackButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroSecondaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextButton
import com.visionsystems.waterreminder.presenter.ui.components.OfflineBanner
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoral
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.asString
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun VerifyEmailScreen() {
    val viewModel: VerifyEmailContract.VerifyEmailViewModel = hiltViewModel<VerifyEmailViewModel>()
    val uiState = viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val noEmailApp = stringResource(R.string.no_email_app)
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            VerifyEmailContract.SideEffect.OpenEmailApp -> {
                val intent = Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_APP_EMAIL)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    snackbarHostState.showSnackbar(noEmailApp)
                }
            }

            is VerifyEmailContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message.asString(context))
        }
    }
    LifecycleResumeEffect(Unit) {
        viewModel.onEventDispatcher(VerifyEmailContract.VerifyEmailEvent.ScreenResumed)
        onPauseOrDispose { }
    }
    BackHandler { viewModel.onEventDispatcher(VerifyEmailContract.VerifyEmailEvent.BackClicked) }
    VerifyEmailContent(uiState = uiState.value, snackbarHostState = snackbarHostState, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun VerifyEmailContent(
    uiState: VerifyEmailContract.VerifyEmailUiState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (VerifyEmailContract.VerifyEmailEvent) -> Unit
) {
    Scaffold(
        containerColor = HydroBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            HydroBackButton(onClick = { onEventDispatcher(VerifyEmailContract.VerifyEmailEvent.BackClicked) })
            OfflineBanner(visible = uiState.isOffline)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(HydroPrimaryTint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(R.drawable.ic_mail), contentDescription = null, tint = HydroPrimary, modifier = Modifier.size(64.dp))
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-12).dp, y = (-16).dp)
                            .size(40.dp),
                        shape = CircleShape,
                        color = HydroCoral,
                        contentColor = Color.White,
                        border = BorderStroke(4.dp, HydroBackground)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(painter = painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Text(text = stringResource(R.string.verify_title), style = MaterialTheme.typography.headlineMedium, color = HydroInk)
                Text(
                    text = stringResource(R.string.verify_body, uiState.email.ifBlank { stringResource(R.string.verify_email_fallback) }),
                    style = MaterialTheme.typography.bodyMedium,
                    color = HydroInkMuted,
                    textAlign = TextAlign.Center
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HydroPrimaryButton(
                    text = stringResource(R.string.action_open_email),
                    onClick = { onEventDispatcher(VerifyEmailContract.VerifyEmailEvent.OpenEmailClicked) }
                )
                HydroSecondaryButton(
                    text = stringResource(R.string.action_verified),
                    enabled = !uiState.isChecking && !uiState.isOffline,
                    onClick = { onEventDispatcher(VerifyEmailContract.VerifyEmailEvent.VerifiedClicked) }
                )
                HydroTextButton(
                    text = stringResource(if (uiState.isResending) R.string.sending else R.string.action_resend),
                    enabled = !uiState.isResending && !uiState.isOffline,
                    onClick = { onEventDispatcher(VerifyEmailContract.VerifyEmailEvent.ResendClicked) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VerifyEmailPreview() {
    HydroTheme {
        VerifyEmailContent(
            uiState = VerifyEmailContract.VerifyEmailUiState(email = "alex@example.com"),
            snackbarHostState = remember { SnackbarHostState() },
            onEventDispatcher = {}
        )
    }
}
