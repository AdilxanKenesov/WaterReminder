package com.visionsystems.waterreminder.presenter.screens.insights

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.AchievementType
import com.visionsystems.waterreminder.domain.module.AchievementUiData
import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import com.visionsystems.waterreminder.domain.util.toLocalDate
import com.visionsystems.waterreminder.presenter.ui.components.HydroCard
import com.visionsystems.waterreminder.presenter.ui.components.SectionTitle
import com.visionsystems.waterreminder.presenter.ui.components.SegmentedToggle
import com.visionsystems.waterreminder.presenter.ui.components.StatTile
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBarSoft
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoral
import com.visionsystems.waterreminder.presenter.ui.theme.HydroDash
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroLocked
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryDark
import com.visionsystems.waterreminder.presenter.ui.theme.HydroSegment
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTrack
import com.visionsystems.waterreminder.presenter.ui.util.rangeLabel
import com.visionsystems.waterreminder.presenter.ui.util.titleRes
import com.visionsystems.waterreminder.presenter.ui.util.currentLocale
import com.visionsystems.waterreminder.presenter.ui.util.dayKeyLetter
import com.visionsystems.waterreminder.presenter.ui.util.label
import com.visionsystems.waterreminder.presenter.ui.util.toVolume
import com.visionsystems.waterreminder.presenter.ui.util.withThousands
import org.orbitmvi.orbit.compose.collectAsState

private const val ACHIEVEMENT_COLUMNS = 4
private const val CHART_HEADROOM = 1.15f
private val MONTH_LABEL_POSITIONS = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)

@Composable
fun InsightsScreen(contentPadding: PaddingValues) {
    val viewModel: InsightsContract.InsightsViewModel = hiltViewModel<InsightsViewModel>()
    val uiState = viewModel.collectAsState()
    InsightsContent(uiState = uiState.value, contentPadding = contentPadding, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun InsightsContent(
    uiState: InsightsContract.InsightsUiState,
    contentPadding: PaddingValues,
    onEventDispatcher: (InsightsContract.InsightsEvent) -> Unit
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
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.insights_title), style = MaterialTheme.typography.headlineMedium, color = HydroInk, modifier = Modifier.weight(1f))
            SegmentedToggle(
                options = InsightsContract.Period.entries,
                selected = uiState.period,
                label = { stringResource(it.title) },
                onSelect = { onEventDispatcher(InsightsContract.InsightsEvent.PeriodSelected(it)) }
            )
        }
        ChartCard(uiState)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = stringResource(R.string.stat_goal_days),
                value = stringResource(R.string.fraction_value, uiState.goalReachedDays, uiState.period.days),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = stringResource(R.string.stat_best_streak),
                value = uiState.bestStreak.toString(),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = stringResource(R.string.stat_glasses_per_day),
                value = String.format(currentLocale(), "%.1f", uiState.drinksPerDay),
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SectionTitle(text = stringResource(R.string.badges), modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.fraction_value, uiState.unlockedCount, uiState.achievements.size),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = HydroInkMuted
            )
        }
        AchievementGrid(uiState.achievements)
    }
}

@Composable
private fun ChartCard(uiState: InsightsContract.InsightsUiState) {
    HydroCard(radius = 24.dp, contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = stringResource(R.string.daily_average), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = HydroInkMuted)
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(text = uiState.averageMl.toVolume(uiState.unit), style = MaterialTheme.typography.displaySmall, color = HydroInk)
                        Text(
                            text = uiState.unit.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HydroInkMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
                Text(text = if (uiState.days.isEmpty()) "" else rangeLabel(uiState.days.first().dayKey, uiState.days.last().dayKey), style = MaterialTheme.typography.labelMedium, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = HydroInkMuted)
            }
            WaterChart(days = uiState.days, goalMl = uiState.goalMl, isWeek = uiState.period == InsightsContract.Period.WEEK)
        }
    }
}

@Composable
private fun WaterChart(days: List<DayTotalUiData>, goalMl: Int, isWeek: Boolean) {
    val maxValue = maxOf(goalMl, days.maxOfOrNull { it.consumedMl } ?: 0, 1) * CHART_HEADROOM
    val grow by animateFloatAsState(targetValue = if (days.isEmpty()) 0f else 1f, animationSpec = tween(700), label = "bars")
    val lastIndex = days.lastIndex
    val chartDescription = stringResource(R.string.chart_description)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .semantics { contentDescription = chartDescription }
            ) {
                if (days.isEmpty()) return@Canvas
                val gap = if (isWeek) 12.dp.toPx() else 3.dp.toPx()
                val barWidth = (size.width - gap * (days.size - 1)) / days.size
                val radius = if (isWeek) 10.dp.toPx() else 3.dp.toPx()
                days.forEachIndexed { index, day ->
                    val color = when {
                        index == lastIndex && !day.isCompleted -> HydroCoral
                        day.isCompleted -> HydroPrimary
                        day.consumedMl > 0 -> HydroBarSoft
                        else -> HydroTrack
                    }
                    val value = if (day.consumedMl > 0) day.consumedMl.toFloat() else maxValue * 0.03f
                    val barHeight = (value / maxValue) * size.height * grow
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(index * (barWidth + gap), size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(radius, radius)
                    )
                }
                if (goalMl > 0) {
                    val y = size.height - (goalMl / maxValue) * size.height
                    drawLine(
                        color = HydroDash,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )
                }
            }
            if (goalMl > 0) {
                Text(
                    text = stringResource(R.string.goal_line, goalMl.withThousands()),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = HydroPrimaryDark,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp)
                )
            }
        }
        if (isWeek) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                days.forEachIndexed { index, day ->
                    ChartLabel(text = day.dayKey.dayKeyLetter(), highlighted = index == lastIndex, modifier = Modifier.weight(1f))
                }
            }
        } else if (days.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MONTH_LABEL_POSITIONS.map { (it * lastIndex).toInt() }.distinct().forEach { index ->
                    ChartLabel(text = days[index].dayKey.toLocalDate().dayOfMonth.toString(), highlighted = index == lastIndex)
                }
            }
        }
    }
}

@Composable
private fun ChartLabel(text: String, highlighted: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 12.sp,
        fontWeight = if (highlighted) FontWeight.Bold else FontWeight.SemiBold,
        color = if (highlighted) HydroInk else HydroInkMuted,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@Composable
private fun AchievementGrid(achievements: List<AchievementUiData>) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        achievements.chunked(ACHIEVEMENT_COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { achievement -> AchievementBadge(achievement, Modifier.weight(1f)) }
                repeat(ACHIEVEMENT_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AchievementBadge(achievement: AchievementUiData, modifier: Modifier = Modifier) {
    val isStreak = achievement.type in setOf(AchievementType.STREAK_3, AchievementType.STREAK_7, AchievementType.STREAK_30)
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val shape = RoundedCornerShape(20.dp)
        Box(
            modifier = Modifier
                .size(58.dp)
                .then(
                    if (achievement.isUnlocked) {
                        Modifier.background(if (isStreak) HydroCoral else HydroPrimary, shape)
                    } else {
                        Modifier
                            .background(HydroSegment, shape)
                            .border(1.dp, HydroDash, shape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(if (achievement.isUnlocked) achievement.type.icon else R.drawable.ic_lock),
                contentDescription = null,
                tint = if (achievement.isUnlocked) Color.White else HydroLocked,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = stringResource(achievement.type.titleRes),
            style = MaterialTheme.typography.labelSmall,
            color = if (achievement.isUnlocked) HydroInk else HydroInkMuted,
            textAlign = TextAlign.Center
        )
    }
}

private val AchievementType.icon: Int
    get() = when (this) {
        AchievementType.FIRST_GLASS -> R.drawable.ic_drop
        AchievementType.FIRST_GOAL -> R.drawable.ic_trophy
        AchievementType.STREAK_3, AchievementType.STREAK_7, AchievementType.STREAK_30 -> R.drawable.ic_fire
        AchievementType.TOTAL_10_LITERS, AchievementType.TOTAL_100_LITERS -> R.drawable.ic_cup
    }

@Preview(showSystemUi = true)
@Composable
private fun InsightsPreview() {
    val consumed = listOf(1760, 1400, 1840, 1680, 1920, 960, 1040)
    HydroTheme {
        InsightsContent(
            uiState = InsightsContract.InsightsUiState(
                isLoading = false,
                days = consumed.mapIndexed { index, ml -> DayTotalUiData(20260916 + index, 2000, ml + if (index < 5) 300 else 0, 7) },
                averageMl = 1920,
                goalMl = 2000,
                goalReachedDays = 5,
                bestStreak = 12,
                drinksPerDay = 7.4f,
                achievements = AchievementType.entries.mapIndexed { index, type -> AchievementUiData(type, if (index < 3) 1L else null) }
            ),
            contentPadding = PaddingValues(),
            onEventDispatcher = {}
        )
    }
}
