package com.visionsystems.waterreminder.presenter.screens.goalreached

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.components.HydroPrimaryButton
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoral
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOnPrimarySoft
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryDark
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import com.visionsystems.waterreminder.presenter.ui.theme.HydroWaterLight
import com.visionsystems.waterreminder.presenter.ui.util.toVolumeText
import org.orbitmvi.orbit.compose.collectAsState

private data class Confetti(val x: Float, val y: Float, val size: Float, val color: Color)

private val ConfettiDots = listOf(
    Confetti(40f, 120f, 10f, HydroCoral),
    Confetti(320f, 90f, 8f, Color.White),
    Confetti(70f, 260f, 6f, HydroWaterLight),
    Confetti(300f, 230f, 12f, HydroCoral),
    Confetti(180f, 70f, 6f, HydroWaterLight),
    Confetti(340f, 340f, 6f, Color.White),
    Confetti(30f, 380f, 8f, Color.White),
    Confetti(250f, 150f, 7f, Color(0xFFFFD08A))
)

@Composable
fun GoalReachedScreen() {
    val viewModel: GoalReachedContract.GoalReachedViewModel = hiltViewModel<GoalReachedViewModel>()
    val uiState = viewModel.collectAsState()
    GoalReachedContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun GoalReachedContent(
    uiState: GoalReachedContract.GoalReachedUiState,
    onEventDispatcher: (GoalReachedContract.GoalReachedEvent) -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val trophyScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.6f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "trophy"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroPrimary)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sx = size.width / 390f
            ConfettiDots.forEach { dot ->
                drawCircle(dot.color, radius = dot.size / 2 * sx, center = Offset(dot.x * sx, dot.y * sx))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically)
            ) {
                Surface(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(trophyScale),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 20.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(painter = painterResource(R.drawable.ic_trophy), contentDescription = null, tint = HydroCoral, modifier = Modifier.size(72.dp))
                    }
                }
                Text(text = stringResource(R.string.goal_reached_title), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, fontSize = 36.sp, color = Color.White)
                Text(
                    text = stringResource(R.string.goal_reached_body, uiState.consumedMl.toVolumeText(uiState.unit)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = HydroOnPrimarySoft,
                    textAlign = TextAlign.Center
                )
                if (uiState.streak > 0) {
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.16f), CircleShape)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_fire), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text(text = pluralStringResource(R.plurals.streak_days, uiState.streak, uiState.streak), style = MaterialTheme.typography.labelLarge, fontSize = 14.sp, color = Color.White)
                    }
                }
            }
            HydroPrimaryButton(
                text = stringResource(R.string.action_done),
                containerColor = Color.White,
                contentColor = HydroPrimaryDark,
                onClick = { onEventDispatcher(GoalReachedContract.GoalReachedEvent.BackHomeClicked) }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GoalReachedPreview() {
    HydroTheme {
        GoalReachedContent(
            uiState = GoalReachedContract.GoalReachedUiState(
                isLoading = false,
                consumedMl = 2000,
                streak = 6
            ),
            onEventDispatcher = {}
        )
    }
}
