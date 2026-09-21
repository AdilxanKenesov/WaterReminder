package com.visionsystems.waterreminder.presenter.screens.onboarding

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.components.HydroTextButton
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBarSoft
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoral
import com.visionsystems.waterreminder.presenter.ui.theme.HydroDash
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.theme.HydroWater
import com.visionsystems.waterreminder.presenter.ui.theme.HydroWaterLight
import kotlinx.coroutines.flow.distinctUntilChanged
import org.orbitmvi.orbit.compose.collectAsState

private data class OnboardingPage(@param:StringRes val title: Int, @param:StringRes val body: Int)

private val Pages = listOf(
    OnboardingPage(R.string.onboarding_title_1, R.string.onboarding_body_1),
    OnboardingPage(R.string.onboarding_title_2, R.string.onboarding_body_2),
    OnboardingPage(R.string.onboarding_title_3, R.string.onboarding_body_3)
)

private val DotColor = Color(0xFFC7E2E0)

@Composable
fun OnboardingScreen() {
    val viewModel: OnboardingContract.OnboardingViewModel = hiltViewModel<OnboardingViewModel>()
    val uiState = viewModel.collectAsState()
    OnboardingContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun OnboardingContent(
    uiState: OnboardingContract.OnboardingUiState,
    onEventDispatcher: (OnboardingContract.OnboardingEvent) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = uiState.page) { uiState.pageCount }
    LaunchedEffect(uiState.page) {
        if (pagerState.currentPage != uiState.page) pagerState.animateScrollToPage(uiState.page)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { onEventDispatcher(OnboardingContract.OnboardingEvent.PageChanged(it)) }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroBackground)
            .safeDrawingPadding()
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 28.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(44.dp), contentAlignment = Alignment.CenterEnd) {
            if (!uiState.isLastPage) {
                HydroTextButton(
                    text = stringResource(R.string.action_skip),
                    color = HydroInkMuted,
                    onClick = { onEventDispatcher(OnboardingContract.OnboardingEvent.SkipClicked) }
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { index ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .background(HydroPrimaryTint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        OnboardingArt(index = index, modifier = Modifier.size(220.dp))
                    }
                }
                Column(
                    modifier = Modifier.padding(bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(Pages[index].title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = HydroInk,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(Pages[index].body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = HydroInkMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        PageDots(active = uiState.page, total = uiState.pageCount, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(24.dp))
        HydroPrimaryButton(
            text = stringResource(if (uiState.isLastPage) R.string.action_get_started else R.string.action_next),
            onClick = { onEventDispatcher(OnboardingContract.OnboardingEvent.NextClicked) }
        )
    }
}

@Composable
private fun PageDots(active: Int, total: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { index ->
            val width by animateDpAsState(if (index == active) 24.dp else 8.dp, label = "dot")
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .background(if (index == active) HydroPrimary else DotColor, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
private fun OnboardingArt(index: Int, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        scale(size.width / 240f, size.height / 240f, pivot = Offset.Zero) {
            when (index) {
                0 -> drawBottleArt()
                1 -> drawReminderArt()
                else -> drawStreakArt()
            }
        }
    }
}

private fun svgPath(data: String): Path = PathParser().parsePathString(data).toPath()

private fun DrawScope.drawBottleArt() {
    val bottle = Path().apply {
        addRoundRect(RoundRect(80f, 62f, 160f, 206f, CornerRadius(24f)))
    }
    drawRoundRect(Color.White, Offset(102f, 40f), Size(36f, 26f), CornerRadius(8f))
    drawRoundRect(HydroPrimary, Offset(102f, 40f), Size(36f, 26f), CornerRadius(8f), style = Stroke(4f))
    drawRoundRect(HydroCoral, Offset(96f, 24f), Size(48f, 18f), CornerRadius(7f))
    drawPath(bottle, Color.White)
    clipPath(bottle) {
        drawPath(svgPath("M70 124 Q90 112 110 124 T150 124 T190 124 V220 H70 Z"), HydroWater)
        drawPath(svgPath("M70 136 Q90 148 110 136 T150 136 T190 136 V220 H70 Z"), HydroWaterLight)
    }
    drawPath(bottle, HydroPrimary, style = Stroke(4f))
    drawPath(svgPath("M48 84s10 11 10 18a10 10 0 0 1-20 0c0-7 10-18 10-18z"), HydroPrimary)
    drawPath(svgPath("M192 58s8 9 8 14a8 8 0 0 1-16 0c0-5 8-14 8-14z"), HydroWater)
    drawPath(svgPath("M190 150s9 10 9 16a9 9 0 0 1-18 0c0-6 9-16 9-16z"), HydroPrimary)
}

private fun DrawScope.drawReminderArt() {
    drawCircle(
        HydroDash,
        radius = 92f,
        center = Offset(120f, 120f),
        style = Stroke(3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f)))
    )
    val bell = svgPath("M78 150V112a42 42 0 1 1 84 0v38l10 14H68l10-14z")
    drawPath(bell, Color.White)
    drawPath(bell, HydroPrimary, style = Stroke(5f, join = StrokeJoin.Round))
    drawPath(svgPath("M106 174a14 14 0 0 0 28 0"), HydroPrimary, style = Stroke(5f, cap = StrokeCap.Round))
    drawCircle(HydroCoral, radius = 26f, center = Offset(176f, 70f))
    drawPath(svgPath("M176 56v14l9 6"), Color.White, style = Stroke(4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(svgPath("M58 60s7 8 7 13a7 7 0 0 1-14 0c0-5 7-13 7-13z"), HydroWater)
}

private fun DrawScope.drawStreakArt() {
    drawRoundRect(Color.White, Offset(40f, 56f), Size(160f, 140f), CornerRadius(24f))
    listOf(
        Triple(62f, 130f, 46f) to HydroBarSoft,
        Triple(95f, 106f, 70f) to HydroPrimary,
        Triple(128f, 88f, 88f) to HydroPrimary,
        Triple(161f, 118f, 58f) to HydroCoral
    ).forEach { (bar, color) ->
        drawRoundRect(color, Offset(bar.first, bar.second), Size(22f, bar.third), CornerRadius(8f))
    }
    drawLine(
        HydroDash,
        Offset(58f, 84f),
        Offset(182f, 84f),
        strokeWidth = 3f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f))
    )
    drawCircle(HydroCoral, radius = 28f, center = Offset(190f, 54f))
    drawPath(svgPath("M190 38c2 5 9 8 9 17a9 9 0 0 1-18 0c0-4 2-6 4-8 0 4 2 5 4 5 0-5-2-9 1-14z"), Color.White)
}

@Preview(showSystemUi = true)
@Composable
private fun OnboardingPreview() {
    HydroTheme {
        OnboardingContent(uiState = OnboardingContract.OnboardingUiState(), onEventDispatcher = {})
    }
}
