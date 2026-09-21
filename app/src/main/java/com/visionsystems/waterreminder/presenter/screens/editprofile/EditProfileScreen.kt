package com.visionsystems.waterreminder.presenter.screens.editprofile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.presenter.ui.components.FieldLabel
import com.visionsystems.waterreminder.presenter.ui.components.HydroBackButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroChipGroup
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextField
import com.visionsystems.waterreminder.presenter.ui.components.UserAvatar
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroDanger
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.asString
import com.visionsystems.waterreminder.presenter.ui.util.labelRes
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun EditProfileScreen() {
    val viewModel: EditProfileContract.EditProfileViewModel = hiltViewModel<EditProfileViewModel>()
    val uiState = viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.onEventDispatcher(EditProfileContract.EditProfileEvent.PhotoPicked(uri.toString()))
    }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            EditProfileContract.SideEffect.OpenPhotoPicker ->
                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            is EditProfileContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message.asString(context))
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        EditProfileContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 88.dp)
        )
    }
}

@Composable
private fun EditProfileContent(
    uiState: EditProfileContract.EditProfileUiState,
    onEventDispatcher: (EditProfileContract.EditProfileEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroBackground)
            .safeDrawingPadding()
            .imePadding()
            .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            HydroBackButton(onClick = { onEventDispatcher(EditProfileContract.EditProfileEvent.BackClicked) })
            Text(text = stringResource(R.string.edit_profile_title), style = MaterialTheme.typography.headlineSmall, color = HydroInk)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EditableAvatar(
                    photo = uiState.photo,
                    name = uiState.fullName,
                    isSaving = uiState.isPhotoSaving,
                    onClick = { onEventDispatcher(EditProfileContract.EditProfileEvent.PhotoClicked) }
                )
                Column {
                    HydroTextButton(
                        text = stringResource(if (uiState.photo == null) R.string.action_add_photo else R.string.action_change_photo),
                        enabled = !uiState.isPhotoSaving,
                        onClick = { onEventDispatcher(EditProfileContract.EditProfileEvent.PhotoClicked) }
                    )
                    if (uiState.hasCustomPhoto) {
                        HydroTextButton(
                            text = stringResource(R.string.action_remove),
                            color = HydroDanger,
                            enabled = !uiState.isPhotoSaving,
                            onClick = { onEventDispatcher(EditProfileContract.EditProfileEvent.RemovePhotoClicked) }
                        )
                    }
                }
            }
            HydroTextField(
                label = stringResource(R.string.field_name),
                value = uiState.fullName,
                onValueChange = { onEventDispatcher(EditProfileContract.EditProfileEvent.NameChanged(it)) },
                placeholder = stringResource(R.string.name_placeholder),
                error = if (uiState.nameError) stringResource(R.string.error_name_empty) else null
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel(stringResource(R.string.field_gender))
                HydroChipGroup(
                    options = Gender.entries,
                    selected = uiState.gender,
                    label = { stringResource(it.labelRes) },
                    onSelect = { onEventDispatcher(EditProfileContract.EditProfileEvent.GenderSelected(it)) }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HydroTextField(
                    label = stringResource(R.string.field_age),
                    value = uiState.age,
                    onValueChange = { onEventDispatcher(EditProfileContract.EditProfileEvent.AgeChanged(it)) },
                    keyboardType = KeyboardType.Number,
                    error = if (uiState.ageError) {
                        stringResource(R.string.range_value, EditProfileContract.AGE_RANGE.first, EditProfileContract.AGE_RANGE.last)
                    } else {
                        null
                    },
                    modifier = Modifier.weight(1f)
                )
                HydroTextField(
                    label = "${stringResource(R.string.field_weight)}, ${stringResource(R.string.unit_kg)}",
                    value = uiState.weight,
                    onValueChange = { onEventDispatcher(EditProfileContract.EditProfileEvent.WeightChanged(it)) },
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    error = if (uiState.weightError) {
                        stringResource(R.string.range_value, EditProfileContract.WEIGHT_RANGE.first, EditProfileContract.WEIGHT_RANGE.last)
                    } else {
                        null
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel(stringResource(R.string.field_activity))
                HydroChipGroup(
                    options = ActivityLevel.entries,
                    selected = uiState.activityLevel,
                    label = { stringResource(it.labelRes) },
                    onSelect = { onEventDispatcher(EditProfileContract.EditProfileEvent.ActivitySelected(it)) }
                )
            }
        }
        HydroPrimaryButton(
            text = stringResource(R.string.action_save),
            enabled = !uiState.isLoading,
            loading = uiState.isSaving,
            onClick = { onEventDispatcher(EditProfileContract.EditProfileEvent.SaveClicked) }
        )
    }
}

@Composable
private fun EditableAvatar(photo: String?, name: String, isSaving: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clickable(enabled = !isSaving, onClick = onClick)
    ) {
        UserAvatar(photo = photo, name = name, size = 72.dp)
        if (isSaving) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(HydroInk.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.5.dp)
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(26.dp),
            shape = CircleShape,
            color = HydroPrimary,
            contentColor = Color.White,
            border = BorderStroke(2.dp, HydroBackground)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(R.drawable.ic_edit), contentDescription = null, modifier = Modifier.size(13.dp))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun EditProfilePreview() {
    HydroTheme {
        EditProfileContent(
            uiState = EditProfileContract.EditProfileUiState(
                isLoading = false,
                fullName = "Alex Morgan",
                gender = Gender.MALE,
                age = "30",
                weight = "70"
            ),
            onEventDispatcher = {}
        )
    }
}
