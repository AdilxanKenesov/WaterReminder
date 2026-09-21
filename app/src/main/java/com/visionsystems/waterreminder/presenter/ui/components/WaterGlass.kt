package com.visionsystems.waterreminder.presenter.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.theme.HydroGlassBack
import com.visionsystems.waterreminder.presenter.ui.theme.HydroWater
import com.visionsystems.waterreminder.presenter.ui.theme.HydroWaterBubble
import com.visionsystems.waterreminder.presenter.ui.theme.HydroWaterLight
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

private const val VIEW_W = 120f
private const val VIEW_H = 170f
private const val GLASS_TOP = 8f
private const val GLASS_BOTTOM = 166f
private const val TWO_PI = (2 * PI).toFloat()

@Composable
fun WaterGlass(
    progress: Float,
    modifier: Modifier = Modifier,
    backColor: Color = HydroGlassBack,
    outlineColor: Color = Color.White
) {
    val level by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), animationSpec = tween(900), label = "level")
    val transition = rememberInfiniteTransition(label = "wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = TWO_PI,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing)),
        label = "phase"
    )
    val description = stringResource(R.string.glass_description, (progress.coerceIn(0f, 1f) * 100).roundToInt())
    Canvas(
        modifier = modifier
            .size(118.dp, 168.dp)
            .semantics { contentDescription = description }
    ) {
        val sx = size.width / VIEW_W
        val sy = size.height / VIEW_H
        val glass = Path().apply {
            moveTo(14 * sx, GLASS_TOP * sy)
            lineTo(106 * sx, GLASS_TOP * sy)
            lineTo(96 * sx, 158 * sy)
            quadraticTo(95 * sx, GLASS_BOTTOM * sy, 87 * sx, GLASS_BOTTOM * sy)
            lineTo(33 * sx, GLASS_BOTTOM * sy)
            quadraticTo(25 * sx, GLASS_BOTTOM * sy, 24 * sx, 158 * sy)
            close()
        }
        clipPath(glass) {
            drawRect(backColor)
            if (level > 0f) {
                val surface = (GLASS_TOP + (1f - level) * (GLASS_BOTTOM - GLASS_TOP)) * sy
                drawPath(wavePath(size.width, size.height, surface, 4 * sy, phase), HydroWater)
                drawPath(wavePath(size.width, size.height, surface + 8 * sy, 4 * sy, phase + PI.toFloat()), HydroWaterLight)
                listOf(Triple(44f, 120f, 4f), Triple(70f, 136f, 3f), Triple(58f, 150f, 2.5f)).forEach { (x, y, r) ->
                    if (y * sy > surface + 14 * sy) {
                        drawCircle(HydroWaterBubble, radius = r * sx, center = Offset(x * sx, y * sy))
                    }
                }
            }
        }
        drawPath(glass, outlineColor, style = Stroke(width = 3 * sx, join = StrokeJoin.Round))
    }
}

private fun wavePath(width: Float, height: Float, baseY: Float, amplitude: Float, phase: Float): Path = Path().apply {
    moveTo(0f, baseY)
    var x = 0f
    while (x <= width) {
        lineTo(x, baseY + amplitude * sin(x / width * TWO_PI * 1.5f + phase))
        x += 3f
    }
    lineTo(width, height)
    lineTo(0f, height)
    close()
}
