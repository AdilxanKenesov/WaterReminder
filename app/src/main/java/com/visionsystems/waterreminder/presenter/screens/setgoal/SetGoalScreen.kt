package com.visionsystems.waterreminder.presenter.screens.setgoal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.presenter.ui.components.FieldLabel
import com.visionsystems.waterreminder.presenter.ui.components.HydroBackButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroCard
import com.visionsystems.waterreminder.presenter.ui.components.HydroChipGroup
import com.visionsystems.waterreminder.presenter.ui.components.HydroCircleButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryDark
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.label
import com.visionsystems.waterreminder.presenter.ui.util.toVolume
import com.visionsystems.waterreminder.presenter.ui.util.toVolumeText
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SetGoalScreen() {
    val viewModel: SetGoalContract.SetGoalViewModel = hiltViewModel<SetGoalViewModel>()
    val uiState = viewModel.collectAsState()
    SetGoalContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun SetGoalContent(
    uiState: SetGoalContract.SetGoalUiState,
    onEventDispatcher: (SetGoalContract.SetGoalEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroBackground)
            .safeDrawingPadding()
            .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            HydroBackButton(onClick = { onEventDispatcher(SetGoalContract.SetGoalEvent.BackClicked) })
            Text(text = stringResource(R.string.goal_title), style = MaterialTheme.typography.headlineSmall, color = HydroInk)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            GoalStepper(uiState, onEventDispatcher)
            if (uiState.cups.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FieldLabel(stringResource(R.string.glass_size))
                    HydroChipGroup(
                        options = uiState.cups,
                        selected = uiState.cups.firstOrNull { it.id == uiState.selectedCupId },
                        label = { cup: CupSizeUiData -> cup.amountMl.toVolume(uiState.unit) },
                        onSelect = { onEventDispatcher(SetGoalContract.SetGoalEvent.CupSelected(it.id)) }
                    )
                }
            }
        }
        HydroPrimaryButton(
            text = stringResource(R.string.action_save),
            enabled = !uiState.isLoading,
            loading = uiState.isSaving,
            onClick = { onEventDispatcher(SetGoalContract.SetGoalEvent.SaveClicked) }
        )
    }
}

@Composable
private fun GoalStepper(uiState: SetGoalContract.SetGoalUiState, onEventDispatcher: (SetGoalContract.SetGoalEvent) -> Unit) {
    HydroCard(radius = 24.dp, contentPadding = PaddingValues(20.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                HydroCircleButton(
                    icon = R.drawable.ic_remove,
                    contentDescription = stringResource(R.string.decrease),
                    size = 52.dp,
                    iconSize = 22.dp,
                    enabled = uiState.goalMl > uiState.minGoalMl,
                    onClick = { onEventDispatcher(SetGoalContract.SetGoalEvent.DecreaseClicked) }
                )
                Text(
                    text = buildAnnotatedString {
                        append(uiState.goalMl.toVolume(uiState.unit))
                        withStyle(SpanStyle(fontSize = 18.sp, color = HydroInkMuted)) { append(" ${uiState.unit.label}") }
                    },
                    style = MaterialTheme.typography.displayMedium,
                    color = HydroInk,
                    modifier = Modifier.weight(1f, fill = false)
                )
                HydroCircleButton(
                    icon = R.drawable.ic_add,
                    contentDescription = stringResource(R.string.increase),
                    size = 52.dp,
                    iconSize = 22.dp,
                    filled = true,
                    enabled = uiState.goalMl < uiState.maxGoalMl,
                    onClick = { onEventDispatcher(SetGoalContract.SetGoalEvent.IncreaseClicked) }
                )
            }
            Surface(
                onClick = { onEventDispatcher(SetGoalContract.SetGoalEvent.UseRecommendedClicked) },
                enabled = uiState.goalMl != uiState.recommendedMl,
                shape = RoundedCornerShape(10.dp),
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.recommended_value, uiState.recommendedMl.toVolumeText(uiState.unit)),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (uiState.goalMl == uiState.recommendedMl) HydroInkMuted else HydroPrimaryDark,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SetGoalPreview() {
    HydroTheme {
        SetGoalContent(
            uiState = SetGoalContract.SetGoalUiState(
                isLoading = false,
                goalMl = 2800,
                recommendedMl = 2800,
                cups = listOf(CupSizeUiData(1, 100, false), CupSizeUiData(2, 200, false), CupSizeUiData(3, 250, true), CupSizeUiData(4, 500, false)),
                selectedCupId = 3
            ),
            onEventDispatcher = {}
        )
    }
}
