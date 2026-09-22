package com.visionsystems.waterreminder.presenter.screens.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.components.HydroCard
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextButton
import com.visionsystems.waterreminder.presenter.ui.components.UserAvatar
import com.visionsystems.waterreminder.presenter.ui.theme.HydroDanger
import com.visionsystems.waterreminder.presenter.ui.theme.HydroDivider
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOnPrimaryMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOnPrimarySoft
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOutline
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryDark
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.asString
import com.visionsystems.waterreminder.presenter.ui.util.label
import com.visionsystems.waterreminder.presenter.ui.util.toVolumeText
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ProfileScreen(contentPadding: PaddingValues) {
    val viewModel: ProfileContract.ProfileViewModel = hiltViewModel<ProfileViewModel>()
    val uiState = viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message.asString(context))
            is ProfileContract.SideEffect.ApplyLanguage ->
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(sideEffect.tag))
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        ProfileContent(uiState = uiState.value, contentPadding = contentPadding, onEventDispatcher = viewModel::onEventDispatcher)
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(contentPadding)
        )
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileContract.ProfileUiState,
    contentPadding: PaddingValues,
    onEventDispatcher: (ProfileContract.ProfileEvent) -> Unit
) {
    val currentLanguage = currentLanguageTag()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 22.dp,
                end = 22.dp,
                top = contentPadding.calculateTopPadding() + 20.dp,
                bottom = contentPadding.calculateBottomPadding() + 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(R.string.profile_title), style = MaterialTheme.typography.headlineMedium, color = HydroInk)
        AccountCard(uiState, onEventDispatcher)
        GoalCard(uiState, onEventDispatcher)
        HydroCard(contentPadding = PaddingValues(0.dp)) {
            SettingsRow(title = stringResource(R.string.settings_units), value = uiState.unit.label) {
                onEventDispatcher(ProfileContract.ProfileEvent.UnitsClicked)
            }
            HorizontalDivider(color = HydroDivider)
            SettingsRow(title = stringResource(R.string.settings_language), value = languageName(currentLanguage)) {
                onEventDispatcher(ProfileContract.ProfileEvent.LanguageClicked)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HydroTextButton(text = stringResource(R.string.action_sign_out), onClick = { onEventDispatcher(ProfileContract.ProfileEvent.SignOutClicked) })
            HydroTextButton(
                text = stringResource(R.string.action_delete_account),
                color = HydroDanger,
                enabled = !uiState.isOffline,
                onClick = { onEventDispatcher(ProfileContract.ProfileEvent.DeleteClicked) }
            )
        }
    }
    if (uiState.showLanguageDialog) LanguageDialog(currentLanguage, onEventDispatcher)
    if (uiState.showDeleteDialog) DeleteAccountDialog(uiState.isDeleting, onEventDispatcher)
}

@Composable
private fun currentLanguageTag(): String =
    if (LocalConfiguration.current.locales[0]?.language == ProfileContract.LANGUAGE_UZBEK) {
        ProfileContract.LANGUAGE_UZBEK
    } else {
        ProfileContract.LANGUAGE_ENGLISH
    }

@Composable
private fun languageName(tag: String): String =
    stringResource(if (tag == ProfileContract.LANGUAGE_UZBEK) R.string.language_uzbek else R.string.language_english)

@Composable
private fun AccountCard(uiState: ProfileContract.ProfileUiState, onEventDispatcher: (ProfileContract.ProfileEvent) -> Unit) {
    HydroCard(radius = 24.dp, contentPadding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            UserAvatar(photo = uiState.photo, name = uiState.name)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = uiState.name.ifBlank { stringResource(R.string.profile_default_name) },
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (uiState.weightKg > 0) {
                    Text(
                        text = stringResource(
                            R.string.profile_summary,
                            uiState.weightKg,
                            pluralStringResource(R.plurals.age_years, uiState.age, uiState.age)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = HydroInkMuted
                    )
                }
                if (uiState.email.isNotBlank()) {
                    Text(text = uiState.email, style = MaterialTheme.typography.bodySmall, color = HydroInkMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Surface(
                onClick = { onEventDispatcher(ProfileContract.ProfileEvent.EditClicked) },
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                contentColor = HydroPrimary,
                border = BorderStroke(1.dp, HydroOutline)
            ) {
                Text(
                    text = stringResource(R.string.action_edit),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun GoalCard(uiState: ProfileContract.ProfileUiState, onEventDispatcher: (ProfileContract.ProfileEvent) -> Unit) {
    Surface(shape = RoundedCornerShape(24.dp), color = HydroPrimary, contentColor = Color.White) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.goal_title),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroOnPrimaryMuted
                )
                Text(text = uiState.goalMl.toVolumeText(uiState.unit), style = MaterialTheme.typography.headlineLarge, color = Color.White)
                Text(
                    text = stringResource(if (uiState.isRecommendedGoal) R.string.goal_recommended else R.string.goal_custom),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = HydroOnPrimarySoft
                )
            }
            Surface(
                onClick = { onEventDispatcher(ProfileContract.ProfileEvent.ChangeGoalClicked) },
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                contentColor = HydroPrimaryDark
            ) {
                Text(
                    text = stringResource(R.string.action_change),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(52.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = HydroInk, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = HydroInkMuted)
        Icon(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = null, tint = HydroInkMuted, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun LanguageDialog(current: String, onEventDispatcher: (ProfileContract.ProfileEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEventDispatcher(ProfileContract.ProfileEvent.LanguageDismissed) },
        containerColor = Color.White,
        title = { Text(text = stringResource(R.string.settings_language), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                listOf(ProfileContract.LANGUAGE_ENGLISH, ProfileContract.LANGUAGE_UZBEK).forEach { tag ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = tag == current,
                                role = Role.RadioButton,
                                onClick = { onEventDispatcher(ProfileContract.ProfileEvent.LanguageSelected(tag)) }
                            )
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tag == current,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = HydroPrimary)
                        )
                        Text(
                            text = languageName(tag),
                            style = MaterialTheme.typography.bodyLarge,
                            color = HydroInk,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onEventDispatcher(ProfileContract.ProfileEvent.LanguageDismissed) }) {
                Text(text = stringResource(R.string.action_cancel), color = HydroInkMuted, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun DeleteAccountDialog(isDeleting: Boolean, onEventDispatcher: (ProfileContract.ProfileEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEventDispatcher(ProfileContract.ProfileEvent.DeleteDismissed) },
        containerColor = Color.White,
        title = { Text(text = stringResource(R.string.delete_title), style = MaterialTheme.typography.titleLarge) },
        text = { Text(text = stringResource(R.string.delete_body), style = MaterialTheme.typography.bodyMedium, color = HydroInkMuted) },
        confirmButton = {
            TextButton(onClick = { onEventDispatcher(ProfileContract.ProfileEvent.DeleteConfirmed) }, enabled = !isDeleting) {
                if (isDeleting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = HydroDanger, strokeWidth = 2.dp)
                } else {
                    Text(text = stringResource(R.string.action_delete), color = HydroDanger, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onEventDispatcher(ProfileContract.ProfileEvent.DeleteDismissed) }, enabled = !isDeleting) {
                Text(text = stringResource(R.string.action_cancel), color = HydroInkMuted, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
private fun ProfilePreview() {
    HydroTheme {
        ProfileContent(
            uiState = ProfileContract.ProfileUiState(
                isLoading = false,
                name = "Alex Morgan",
                email = "alex@example.com",
                weightKg = 70,
                age = 30,
                goalMl = 2800
            ),
            contentPadding = PaddingValues(),
            onEventDispatcher = {}
        )
    }
}
