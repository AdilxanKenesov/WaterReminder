package com.visionsystems.waterreminder.presenter.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import com.visionsystems.waterreminder.domain.module.DrinkEntryUiData
import com.visionsystems.waterreminder.presenter.ui.components.HydroCard
import com.visionsystems.waterreminder.presenter.ui.components.IconBadge
import com.visionsystems.waterreminder.presenter.ui.components.SectionTitle
import com.visionsystems.waterreminder.presenter.ui.components.WaterGlass
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoral
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoralInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoralTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroDanger
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOnPrimaryMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOnPrimarySoft
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOutline
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryContainer
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryDark
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroSurface
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTrack
import com.visionsystems.waterreminder.presenter.ui.util.asString
import com.visionsystems.waterreminder.presenter.ui.util.dayKeyLetter
import com.visionsystems.waterreminder.presenter.ui.util.firstName
import com.visionsystems.waterreminder.presenter.ui.util.greetingRes
import com.visionsystems.waterreminder.presenter.ui.util.label
import com.visionsystems.waterreminder.presenter.ui.util.toClock
import com.visionsystems.waterreminder.presenter.ui.util.toDayHeader
import com.visionsystems.waterreminder.presenter.ui.util.toHm
import com.visionsystems.waterreminder.presenter.ui.util.toVolume
import com.visionsystems.waterreminder.presenter.ui.util.toVolumeText
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun HomeScreen(contentPadding: PaddingValues) {
    val viewModel: HomeContract.HomeViewModel = hiltViewModel<HomeViewModel>()
    val uiState = viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val undoLabel = stringResource(R.string.action_undo)
    viewModel.collectSideEffect { sideEffect ->
        snackbarHostState.currentSnackbarData?.dismiss()
        when (sideEffect) {
            is HomeContract.SideEffect.ShowAdded -> {
                val result = snackbarHostState.showSnackbar(
                    message = sideEffect.message.asString(context),
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onEventDispatcher(HomeContract.HomeEvent.UndoClicked(sideEffect.entryId))
                }
            }

            is HomeContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message.asString(context))
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        HomeContent(uiState = uiState.value, contentPadding = contentPadding, onEventDispatcher = viewModel::onEventDispatcher)
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(contentPadding)
        )
    }
}

@Composable
private fun HomeContent(
    uiState: HomeContract.HomeUiState,
    contentPadding: PaddingValues,
    onEventDispatcher: (HomeContract.HomeEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 22.dp,
            end = 22.dp,
            top = contentPadding.calculateTopPadding() + 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item(key = "header") { HomeHeader(uiState) }
        item(key = "hero") { ProgressHero(uiState) }
        item(key = "quick") { QuickAdd(uiState, onEventDispatcher) }
        item(key = "streak") { StreakCard(uiState) }
        item(key = "log_title") { SectionTitle(text = stringResource(R.string.log_title)) }
        if (uiState.entries.isEmpty() && !uiState.isLoading) {
            item(key = "empty") {
                Text(text = stringResource(R.string.log_empty), style = MaterialTheme.typography.bodyMedium, color = HydroInkMuted)
            }
        }
        items(uiState.entries, key = { it.id }) { entry ->
            SwipeableLogRow(
                entry = entry,
                amountText = entry.amountMl.toVolumeText(uiState.unit),
                onDeleteRequest = { onEventDispatcher(HomeContract.HomeEvent.DeleteRequested(entry.id)) },
                modifier = Modifier.animateItem()
            )
        }
    }
    uiState.pendingDelete?.let { entry ->
        DeleteEntryDialog(
            text = "${entry.amountMl.toVolumeText(uiState.unit)} · ${entry.timestamp.toClock()}",
            onConfirm = { onEventDispatcher(HomeContract.HomeEvent.DeleteConfirmed) },
            onDismiss = { onEventDispatcher(HomeContract.HomeEvent.DeleteDismissed) }
        )
    }
}

@Composable
private fun DeleteEntryDialog(text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(text = stringResource(R.string.delete_entry_title), style = MaterialTheme.typography.titleLarge) },
        text = { Text(text = text, style = MaterialTheme.typography.bodyMedium, color = HydroInkMuted) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.action_delete), color = HydroDanger, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel), color = HydroInkMuted, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun HomeHeader(uiState: HomeContract.HomeUiState) {
    val now = uiState.now ?: return
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = now.toLocalDate().toDayHeader(), style = MaterialTheme.typography.bodySmall, color = HydroInkMuted)
        val greeting = stringResource(greetingRes(now.toLocalTime()))
        val name = uiState.userName.firstName()
        Text(
            text = if (name.isBlank()) greeting else stringResource(R.string.name_with_greeting, greeting, name),
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 25.sp,
            color = HydroInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProgressHero(uiState: HomeContract.HomeUiState) {
    Surface(shape = RoundedCornerShape(28.dp), color = HydroPrimary, contentColor = Color.White) {
        Row(
            modifier = Modifier.padding(start = 22.dp, end = 20.dp, top = 22.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.today).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.4.sp,
                    color = HydroOnPrimaryMuted
                )
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = uiState.consumedMl.toVolume(uiState.unit), style = MaterialTheme.typography.displayMedium, fontSize = 46.sp, lineHeight = 46.sp)
                    Text(
                        text = uiState.unit.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroOnPrimaryMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.of_goal, uiState.goalMl.toVolumeText(uiState.unit)),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    color = HydroOnPrimarySoft
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${uiState.percent}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = HydroPrimary,
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                    Text(
                        text = reminderText(uiState.nextReminder, uiState.isCompleted),
                        style = MaterialTheme.typography.bodySmall,
                        color = HydroOnPrimarySoft,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            WaterGlass(progress = uiState.progress)
        }
    }
}

@Composable
private fun reminderText(next: LocalTime?, completed: Boolean): String = when {
    completed -> stringResource(R.string.goal_done)
    next != null -> stringResource(R.string.next_reminder_at, next.toHm())
    else -> stringResource(R.string.reminders_off)
}

@Composable
private fun QuickAdd(uiState: HomeContract.HomeUiState, onEventDispatcher: (HomeContract.HomeEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(text = stringResource(R.string.quick_add))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            uiState.quickCups.forEach { cup ->
                QuickCupButton(
                    cup = cup,
                    valueText = cup.amountMl.toVolume(uiState.unit),
                    unitText = uiState.unit.label,
                    enabled = !uiState.isAdding,
                    onClick = { onEventDispatcher(HomeContract.HomeEvent.QuickAdd(cup.amountMl)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickCupButton(
    cup: CupSizeUiData,
    valueText: String,
    unitText: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(18.dp),
        color = if (cup.isSelected) HydroPrimaryTint else HydroSurface,
        contentColor = if (cup.isSelected) HydroPrimaryDark else HydroInk,
        border = if (cup.isSelected) BorderStroke(2.dp, HydroPrimary) else BorderStroke(1.dp, HydroOutline)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "+$valueText", style = MaterialTheme.typography.titleSmall)
            Text(text = unitText, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp, color = if (cup.isSelected) HydroPrimaryDark else HydroInkMuted)
        }
    }
}

@Composable
private fun StreakCard(uiState: HomeContract.HomeUiState) {
    HydroCard(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconBadge(icon = R.drawable.ic_fire, background = HydroCoralTint, tint = HydroCoralInk)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (uiState.streak > 0) {
                            pluralStringResource(R.plurals.streak_days, uiState.streak, uiState.streak)
                        } else {
                            stringResource(R.string.streak_start)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = HydroInk
                    )
                    Text(
                        text = if (uiState.isCompleted) {
                            stringResource(R.string.streak_safe)
                        } else {
                            stringResource(R.string.left_today, uiState.remainingMl.toVolumeText(uiState.unit))
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = HydroInkMuted
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                uiState.week.forEach { day -> WeekDot(day = day, isToday = day.dayKey == uiState.todayKey) }
            }
        }
    }
}

@Composable
private fun WeekDot(day: DayTotalUiData, isToday: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val modifier = Modifier.size(30.dp)
        when {
            day.isCompleted -> Box(modifier.background(HydroPrimary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(R.drawable.ic_check), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            isToday -> Box(
                modifier
                    .background(HydroSurface, CircleShape)
                    .border(2.5.dp, HydroCoral, CircleShape)
            )
            day.consumedMl > 0 -> Box(modifier.background(HydroPrimaryContainer, CircleShape))
            else -> Box(modifier.background(HydroTrack.copy(alpha = 0.6f), CircleShape))
        }
        Text(
            text = day.dayKey.dayKeyLetter(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isToday) HydroInk else HydroInkMuted
        )
    }
}

@Composable
private fun SwipeableLogRow(
    entry: DrinkEntryUiData,
    amountText: String,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val threshold = SwipeToDismissBoxDefaults.positionalThreshold
    val dismissState = remember(entry.id) { SwipeToDismissBoxState(SwipeToDismissBoxValue.Settled, threshold) }
    val scope = rememberCoroutineScope()
    val requestDelete by rememberUpdatedState(onDeleteRequest)
    val onDismiss: (SwipeToDismissBoxValue) -> Unit = remember(dismissState) {
        {
            requestDelete()
            scope.launch { dismissState.snapTo(SwipeToDismissBoxValue.Settled) }
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        onDismiss = onDismiss,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HydroDanger.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.action_delete),
                    tint = HydroDanger,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    ) {
        HydroCard(modifier = Modifier.fillMaxWidth(), radius = 18.dp, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconBadge(icon = R.drawable.ic_drop, background = HydroPrimaryTint, tint = HydroPrimary, size = 38.dp, radius = 12.dp, iconSize = 18.dp)
                Text(text = amountText, style = MaterialTheme.typography.titleSmall, fontSize = 15.sp, color = HydroInk, modifier = Modifier.weight(1f))
                Text(text = entry.timestamp.toClock(), style = MaterialTheme.typography.bodySmall, color = HydroInkMuted)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomePreview() {
    val today = 20260922
    HydroTheme {
        HomeContent(
            uiState = HomeContract.HomeUiState(
                isLoading = false,
                now = LocalDateTime.of(2026, 9, 22, 9, 30),
                userName = "Alex",
                consumedMl = 1250,
                goalMl = 2000,
                progress = 0.62f,
                remainingMl = 750,
                streak = 5,
                todayKey = today,
                week = (16..22).map { DayTotalUiData(20260900 + it, 2000, if (it < 21) 2100 else if (it == 21) 900 else 1250, 6) },
                quickCups = listOf(
                    CupSizeUiData(1, 100, false),
                    CupSizeUiData(2, 200, false),
                    CupSizeUiData(3, 250, true),
                    CupSizeUiData(4, 500, false)
                ),
                entries = listOf(DrinkEntryUiData(2, 250, 1_758_540_300_000, today), DrinkEntryUiData(1, 500, 1_758_532_800_000, today)),
                nextReminder = LocalTime.of(14, 30)
            ),
            contentPadding = PaddingValues(),
            onEventDispatcher = {}
        )
    }
}
