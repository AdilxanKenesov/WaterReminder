package com.visionsystems.waterreminder.presenter.screens.reminders

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.ReminderSlotState
import com.visionsystems.waterreminder.domain.module.ReminderSlotUiData
import com.visionsystems.waterreminder.presenter.ui.components.FieldLabel
import com.visionsystems.waterreminder.presenter.ui.components.HydroCard
import com.visionsystems.waterreminder.presenter.ui.components.HydroChipGroup
import com.visionsystems.waterreminder.presenter.ui.components.HydroSwitch
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTimePickerDialog
import com.visionsystems.waterreminder.presenter.ui.components.IconBadge
import com.visionsystems.waterreminder.presenter.ui.components.ToggleRow
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoralInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoralTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroMoonInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroMoonTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOnPrimaryMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOutline
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryDark
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroSunInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroSunTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroSurface
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.util.intervalText
import com.visionsystems.waterreminder.presenter.ui.util.toHm
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalTime

private const val PLAN_COLUMNS = 4

@Composable
fun RemindersScreen(contentPadding: PaddingValues) {
    val viewModel: RemindersContract.RemindersViewModel = hiltViewModel<RemindersViewModel>()
    val uiState = viewModel.collectAsState()
    val context = LocalContext.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            RemindersContract.SideEffect.OpenNotificationSettings -> {
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                }
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }
    RemindersContent(uiState = uiState.value, contentPadding = contentPadding, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun RemindersContent(
    uiState: RemindersContract.RemindersUiState,
    contentPadding: PaddingValues,
    onEventDispatcher: (RemindersContract.RemindersEvent) -> Unit
) {
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
        Text(text = stringResource(R.string.reminders_title), style = MaterialTheme.typography.headlineMedium, color = HydroInk)
        if (!uiState.canNotify) PermissionCard(onClick = { onEventDispatcher(RemindersContract.RemindersEvent.EnableNotificationsClicked) })
        NextReminderCard(uiState, onEventDispatcher)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TimeTile(
                label = stringResource(R.string.field_wake),
                time = uiState.wakeTime,
                icon = R.drawable.ic_sunny,
                tint = HydroSunTint,
                ink = HydroSunInk,
                onClick = { onEventDispatcher(RemindersContract.RemindersEvent.PickerOpened(RemindersContract.TimeTarget.WAKE)) },
                modifier = Modifier.weight(1f)
            )
            TimeTile(
                label = stringResource(R.string.field_bedtime),
                time = uiState.sleepTime,
                icon = R.drawable.ic_bedtime,
                tint = HydroMoonTint,
                ink = HydroMoonInk,
                onClick = { onEventDispatcher(RemindersContract.RemindersEvent.PickerOpened(RemindersContract.TimeTarget.SLEEP)) },
                modifier = Modifier.weight(1f)
            )
        }
        if (uiState.slots.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel(stringResource(R.string.today))
                uiState.slots.chunked(PLAN_COLUMNS).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { slot -> PlanChip(slot, dimmed = !uiState.config.enabled, modifier = Modifier.weight(1f)) }
                        repeat(PLAN_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FieldLabel(stringResource(R.string.frequency))
            HydroChipGroup(
                options = RemindersContract.FREQUENCIES,
                selected = uiState.config.intervalMinutes,
                label = { minutes -> if (minutes == null) stringResource(R.string.frequency_auto) else intervalText(minutes) },
                onSelect = { onEventDispatcher(RemindersContract.RemindersEvent.FrequencySelected(it)) }
            )
            if (uiState.config.intervalMinutes == null) {
                Text(
                    text = stringResource(R.string.every_interval, intervalText(uiState.intervalMinutes)),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = HydroInkMuted
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FieldLabel(stringResource(R.string.notifications))
            HydroCard(contentPadding = PaddingValues(0.dp)) {
                ToggleRow(
                    title = stringResource(R.string.notify_goal),
                    checked = uiState.config.goalNotify,
                    onCheckedChange = { onEventDispatcher(RemindersContract.RemindersEvent.GoalNotifyChanged(it)) }
                )
                ToggleRow(
                    title = stringResource(R.string.notify_streak),
                    checked = uiState.config.streakNotify,
                    onCheckedChange = { onEventDispatcher(RemindersContract.RemindersEvent.StreakNotifyChanged(it)) }
                )
                ToggleRow(
                    title = stringResource(R.string.notify_badges),
                    checked = uiState.config.achievementNotify,
                    onCheckedChange = { onEventDispatcher(RemindersContract.RemindersEvent.AchievementNotifyChanged(it)) }
                )
                ToggleRow(
                    title = stringResource(R.string.notify_sound),
                    checked = uiState.config.soundEnabled,
                    onCheckedChange = { onEventDispatcher(RemindersContract.RemindersEvent.SoundChanged(it)) },
                    showDivider = false
                )
            }
        }
    }
    uiState.picker?.let { target ->
        HydroTimePickerDialog(
            title = stringResource(if (target == RemindersContract.TimeTarget.WAKE) R.string.field_wake else R.string.field_bedtime),
            initial = if (target == RemindersContract.TimeTarget.WAKE) uiState.wakeTime else uiState.sleepTime,
            onConfirm = { onEventDispatcher(RemindersContract.RemindersEvent.TimePicked(it)) },
            onDismiss = { onEventDispatcher(RemindersContract.RemindersEvent.PickerDismissed) }
        )
    }
}

@Composable
private fun NextReminderCard(uiState: RemindersContract.RemindersUiState, onEventDispatcher: (RemindersContract.RemindersEvent) -> Unit) {
    Surface(shape = RoundedCornerShape(24.dp), color = HydroPrimary, contentColor = Color.White) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconBadge(icon = R.drawable.ic_alarm, background = Color.White.copy(alpha = 0.16f), tint = Color.White, size = 46.dp, radius = 16.dp, iconSize = 24.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.next_reminder),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroOnPrimaryMuted
                )
                Text(
                    text = when {
                        !uiState.config.enabled -> stringResource(R.string.state_paused)
                        uiState.goalCompleted -> stringResource(R.string.state_done_today)
                        else -> uiState.nextReminder?.toHm() ?: "—"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
            HydroSwitch(
                checked = uiState.config.enabled,
                onCheckedChange = { onEventDispatcher(RemindersContract.RemindersEvent.EnabledChanged(it)) },
                onPrimary = true
            )
        }
    }
}

@Composable
private fun TimeTile(
    label: String,
    time: LocalTime,
    icon: Int,
    tint: Color,
    ink: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HydroCard(modifier = modifier, radius = 18.dp, contentPadding = PaddingValues(14.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconBadge(icon = icon, background = tint, tint = ink, size = 36.dp, radius = 12.dp, iconSize = 18.dp)
            Column {
                Text(text = label, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = HydroInkMuted)
                Text(text = time.toHm(), style = MaterialTheme.typography.titleSmall, color = HydroInk)
            }
        }
    }
}

@Composable
private fun PlanChip(slot: ReminderSlotUiData, dimmed: Boolean, modifier: Modifier = Modifier) {
    val (background, content, border) = when (slot.state) {
        ReminderSlotState.DONE -> Triple(HydroPrimaryTint, HydroPrimaryDark, null)
        ReminderSlotState.NEXT -> Triple(HydroPrimary, Color.White, null)
        ReminderSlotState.LATER -> Triple(HydroSurface, HydroInk, BorderStroke(1.dp, HydroOutline))
    }
    Surface(
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (dimmed) background.copy(alpha = 0.5f) else background,
        contentColor = content,
        border = border
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            if (slot.state == ReminderSlotState.DONE) {
                Icon(painter = painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(14.dp))
            }
            Text(
                text = slot.time.toHm(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (slot.state == ReminderSlotState.LATER) FontWeight.SemiBold else FontWeight.Bold,
                modifier = Modifier.padding(start = if (slot.state == ReminderSlotState.DONE) 4.dp else 0.dp)
            )
        }
    }
}

@Composable
private fun PermissionCard(onClick: () -> Unit) {
    HydroCard(color = HydroCoralTint, contentColor = HydroInk, contentPadding = PaddingValues(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(R.drawable.ic_bell), contentDescription = null, tint = HydroCoralInk, modifier = Modifier.size(20.dp))
            }
            Text(
                text = stringResource(R.string.notifications_off),
                style = MaterialTheme.typography.titleSmall,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            HydroTextButton(text = stringResource(R.string.action_turn_on), color = HydroCoralInk, onClick = onClick)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun RemindersPreview() {
    HydroTheme {
        RemindersContent(
            uiState = RemindersContract.RemindersUiState(
                isLoading = false,
                nextReminder = LocalTime.of(15, 0),
                slots = listOf(9, 11, 13, 15, 17, 19, 21).map { hour ->
                    ReminderSlotUiData(
                        LocalTime.of(hour, 0),
                        when {
                            hour < 15 -> ReminderSlotState.DONE
                            hour == 15 -> ReminderSlotState.NEXT
                            else -> ReminderSlotState.LATER
                        }
                    )
                }
            ),
            contentPadding = PaddingValues(),
            onEventDispatcher = {}
        )
    }
}
