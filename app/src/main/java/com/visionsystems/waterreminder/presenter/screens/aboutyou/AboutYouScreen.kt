package com.visionsystems.waterreminder.presenter.screens.aboutyou

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.presenter.ui.components.FieldLabel
import com.visionsystems.waterreminder.presenter.ui.components.HydroChipGroup
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.components.ScreenTitle
import com.visionsystems.waterreminder.presenter.ui.components.StepHeader
import com.visionsystems.waterreminder.presenter.ui.components.ValueSliderCard
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.labelRes
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun AboutYouScreen() {
    val viewModel: AboutYouContract.AboutYouViewModel = hiltViewModel<AboutYouViewModel>()
    val uiState = viewModel.collectAsState()
    BackHandler { viewModel.onEventDispatcher(AboutYouContract.AboutYouEvent.BackClicked) }
    AboutYouContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun AboutYouContent(
    uiState: AboutYouContract.AboutYouUiState,
    onEventDispatcher: (AboutYouContract.AboutYouEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroBackground)
            .safeDrawingPadding()
            .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(step = 1, total = 2, onBack = { onEventDispatcher(AboutYouContract.AboutYouEvent.BackClicked) })
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScreenTitle(title = stringResource(R.string.about_title), subtitle = stringResource(R.string.about_subtitle))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel(stringResource(R.string.field_gender))
                HydroChipGroup(
                    options = Gender.entries,
                    selected = uiState.gender,
                    label = { stringResource(it.labelRes) },
                    onSelect = { onEventDispatcher(AboutYouContract.AboutYouEvent.GenderSelected(it)) }
                )
            }
            ValueSliderCard(
                label = stringResource(R.string.field_age),
                value = uiState.age,
                unit = stringResource(R.string.unit_years),
                range = AboutYouContract.AGE_RANGE,
                onValueChange = { onEventDispatcher(AboutYouContract.AboutYouEvent.AgeChanged(it)) }
            )
            ValueSliderCard(
                label = stringResource(R.string.field_weight),
                value = uiState.weightKg,
                unit = stringResource(R.string.unit_kg),
                range = AboutYouContract.WEIGHT_RANGE,
                onValueChange = { onEventDispatcher(AboutYouContract.AboutYouEvent.WeightChanged(it)) }
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel(stringResource(R.string.field_activity))
                HydroChipGroup(
                    options = ActivityLevel.entries,
                    selected = uiState.activityLevel,
                    label = { stringResource(it.labelRes) },
                    onSelect = { onEventDispatcher(AboutYouContract.AboutYouEvent.ActivitySelected(it)) }
                )
            }
        }
        HydroPrimaryButton(
            text = stringResource(R.string.action_continue),
            enabled = uiState.gender != null,
            loading = uiState.isSaving,
            onClick = { onEventDispatcher(AboutYouContract.AboutYouEvent.ContinueClicked) }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AboutYouPreview() {
    HydroTheme {
        AboutYouContent(uiState = AboutYouContract.AboutYouUiState(gender = Gender.MALE), onEventDispatcher = {})
    }
}
