package com.visionsystems.waterreminder.presenter.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.components.HydroCard
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTimePickerDialog
import com.visionsystems.waterreminder.presenter.ui.components.IconBadge
import com.visionsystems.waterreminder.presenter.ui.components.ScreenTitle
import com.visionsystems.waterreminder.presenter.ui.components.StepHeader
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroMoonInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroMoonTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroSunInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroSunTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTrack
import com.visionsystems.waterreminder.presenter.ui.util.intervalText
import com.visionsystems.waterreminder.presenter.ui.util.toHm
import org.orbitmvi.orbit.compose.collectAsState
import java.time.LocalTime

private const val MAX_TIMELINE_POINTS = 7

@Composable
fun ScheduleScreen() {
    val viewModel: ScheduleContract.ScheduleViewModel = hiltViewModel<ScheduleViewModel>()
    val uiState = viewModel.collectAsState()
    ScheduleContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun ScheduleContent(
    uiState: ScheduleContract.ScheduleUiState,
    onEventDispatcher: (ScheduleContract.ScheduleEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroBackground)
            .safeDrawingPadding()
            .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        StepHeader(step = 2, total = 2, onBack = { onEventDispatcher(ScheduleContract.ScheduleEvent.BackClicked) })
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ScreenTitle(title = stringResource(R.string.schedule_title), subtitle = stringResource(R.string.schedule_subtitle))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TimeCard(
                    label = stringResource(R.string.field_wake),
                    time = uiState.wakeTime,
                    icon = R.drawable.ic_sunny,
                    tint = HydroSunTint,
                    ink = HydroSunInk,
                    onClick = { onEventDispatcher(ScheduleContract.ScheduleEvent.PickerOpened(ScheduleContract.TimeTarget.WAKE)) },
                    modifier = Modifier.weight(1f)
                )
                TimeCard(
                    label = stringResource(R.string.field_bedtime),
                    time = uiState.sleepTime,
                    icon = R.drawable.ic_bedtime,
                    tint = HydroMoonTint,
                    ink = HydroMoonInk,
                    onClick = { onEventDispatcher(ScheduleContract.ScheduleEvent.PickerOpened(ScheduleContract.TimeTarget.SLEEP)) },
                    modifier = Modifier.weight(1f)
                )
            }
            PlanCard(intervalMinutes = uiState.intervalMinutes, slots = uiState.slots)
        }
        HydroPrimaryButton(
            text = stringResource(R.string.action_continue),
            loading = uiState.isSaving,
            onClick = { onEventDispatcher(ScheduleContract.ScheduleEvent.ContinueClicked) }
        )
    }
    uiState.picker?.let { target ->
        HydroTimePickerDialog(
            title = stringResource(if (target == ScheduleContract.TimeTarget.WAKE) R.string.field_wake else R.string.field_bedtime),
            initial = if (target == ScheduleContract.TimeTarget.WAKE) uiState.wakeTime else uiState.sleepTime,
            onConfirm = { onEventDispatcher(ScheduleContract.ScheduleEvent.TimePicked(it)) },
            onDismiss = { onEventDispatcher(ScheduleContract.ScheduleEvent.PickerDismissed) }
        )
    }
}

@Composable
private fun TimeCard(
    label: String,
    time: LocalTime,
    icon: Int,
    tint: Color,
    ink: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HydroCard(modifier = modifier, onClick = onClick, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            IconBadge(icon = icon, background = tint, tint = ink)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = HydroInkMuted)
                Text(text = time.toHm(), style = MaterialTheme.typography.headlineLarge, color = HydroInk)
            }
        }
    }
}

@Composable
private fun PlanCard(intervalMinutes: Int, slots: List<LocalTime>) {
    HydroCard(contentPadding = PaddingValues(18.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconBadge(icon = R.drawable.ic_bell, background = HydroPrimaryTint, tint = HydroPrimary, size = 38.dp, radius = 12.dp, iconSize = 20.dp)
                Column {
                    Text(text = stringResource(R.string.every_interval, intervalText(intervalMinutes)), style = MaterialTheme.typography.titleSmall, fontSize = 15.sp, color = HydroInk)
                    Text(
                        text = pluralStringResource(R.plurals.reminders_per_day, slots.size, slots.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = HydroInkMuted
                    )
                }
            }
            if (slots.isNotEmpty()) Timeline(slots = slots)
        }
    }
}

@Composable
private fun Timeline(slots: List<LocalTime>) {
    val points = if (slots.size <= MAX_TIMELINE_POINTS) slots else {
        val step = (slots.size - 1).toFloat() / (MAX_TIMELINE_POINTS - 1)
        (0 until MAX_TIMELINE_POINTS).map { slots[(it * step).toInt()] }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(2.dp)
                .background(HydroTrack)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            points.forEach { time ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(HydroPrimary, CircleShape)
                    )
                    Text(text = time.toHm().take(2), style = MaterialTheme.typography.labelSmall, color = HydroInkMuted)
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SchedulePreview() {
    HydroTheme {
        ScheduleContent(
            uiState = ScheduleContract.ScheduleUiState(
                slots = (1..7).map { LocalTime.of(7, 0).plusHours(it * 2L) }
            ),
            onEventDispatcher = {}
        )
    }
}
