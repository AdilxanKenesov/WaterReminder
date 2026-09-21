package com.visionsystems.waterreminder.presenter.screens.goalready

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.components.GoalRing
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroSecondaryButton
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.withThousands
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun GoalReadyScreen() {
    val viewModel: GoalReadyContract.GoalReadyViewModel = hiltViewModel<GoalReadyViewModel>()
    val uiState = viewModel.collectAsState()
    GoalReadyContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun GoalReadyContent(
    uiState: GoalReadyContract.GoalReadyUiState,
    onEventDispatcher: (GoalReadyContract.GoalReadyEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroBackground)
            .safeDrawingPadding()
            .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterVertically)
        ) {
            GoalRing(progress = if (uiState.isLoading) 0f else 0.75f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (uiState.isLoading) "—" else uiState.goalMl.withThousands(),
                        style = MaterialTheme.typography.displayMedium,
                        color = HydroInk
                    )
                    Text(text = stringResource(R.string.per_day, "ml"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = HydroInkMuted)
                }
            }
            Text(text = stringResource(R.string.goal_title), style = MaterialTheme.typography.headlineMedium, color = HydroInk)
            if (!uiState.isLoading) {
                Text(
                    text = pluralStringResource(R.plurals.glasses_of, uiState.glasses, uiState.glasses, "${uiState.cupMl} ml"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = HydroInkMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HydroPrimaryButton(
                text = stringResource(R.string.action_start),
                enabled = !uiState.isLoading,
                onClick = { onEventDispatcher(GoalReadyContract.GoalReadyEvent.StartClicked) }
            )
            HydroSecondaryButton(
                text = stringResource(R.string.action_adjust),
                enabled = !uiState.isLoading,
                onClick = { onEventDispatcher(GoalReadyContract.GoalReadyEvent.AdjustClicked) }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GoalReadyPreview() {
    HydroTheme {
        GoalReadyContent(
            uiState = GoalReadyContract.GoalReadyUiState(isLoading = false, goalMl = 2800, weightKg = 70),
            onEventDispatcher = {}
        )
    }
}
