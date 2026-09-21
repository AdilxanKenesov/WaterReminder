package com.visionsystems.waterreminder.presenter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.theme.HydroDivider
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOutline
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryDark
import com.visionsystems.waterreminder.presenter.ui.theme.HydroSegment
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTrack
import com.visionsystems.waterreminder.presenter.ui.theme.Manrope
import kotlin.math.roundToInt

@Composable
fun HydroSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    onPrimary: Boolean = false
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = if (onPrimary) {
            SwitchDefaults.colors(
                checkedThumbColor = HydroPrimary,
                checkedTrackColor = Color.White,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.White.copy(alpha = 0.25f),
                uncheckedBorderColor = Color.White.copy(alpha = 0.6f)
            )
        } else {
            SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = HydroPrimary,
                uncheckedThumbColor = HydroInkMuted,
                uncheckedTrackColor = HydroSegment,
                uncheckedBorderColor = HydroTrack
            )
        }
    )
}

@Composable
fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    showDivider: Boolean = true,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = HydroInk)
                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp, color = HydroInkMuted)
                }
            }
            HydroSwitch(checked = checked, onCheckedChange = null)
        }
        if (showDivider) HorizontalDivider(color = HydroDivider, thickness = 1.dp)
    }
}

@Composable
fun ValueSliderCard(
    label: String,
    value: Int,
    unit: String,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    step: Int = 1
) {
    HydroCard(modifier = modifier.fillMaxWidth(), radius = 18.dp, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(
                text = buildAnnotatedString {
                    append(value.toString())
                    withStyle(SpanStyle(fontFamily = Manrope, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = HydroInkMuted)) {
                        append(" $unit")
                    }
                },
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp
            )
        }
        HydroSlider(value = value, range = range, onValueChange = onValueChange, step = step)
    }
}

@Composable
fun HydroSlider(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    step: Int = 1
) {
    val steps = ((range.last - range.first) / step - 1).coerceAtLeast(0)
    Slider(
        value = value.toFloat(),
        onValueChange = { raw ->
            val snapped = range.first + ((raw - range.first) / step).roundToInt() * step
            onValueChange(snapped.coerceIn(range))
        },
        valueRange = range.first.toFloat()..range.last.toFloat(),
        steps = if (steps > 200) 0 else steps,
        modifier = modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = HydroPrimary,
            activeTrackColor = HydroPrimary,
            inactiveTrackColor = HydroTrack,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        )
    )
}

@Composable
fun <T> SegmentedToggle(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(HydroSegment, CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Surface(
                onClick = { onSelect(option) },
                shape = CircleShape,
                color = if (isSelected) Color.White else Color.Transparent,
                contentColor = if (isSelected) HydroPrimaryDark else HydroInkMuted,
                modifier = Modifier
                    .height(36.dp)
                    .then(
                        if (isSelected) {
                            Modifier.shadow(
                                elevation = 1.dp,
                                shape = CircleShape,
                                ambientColor = HydroInk.copy(alpha = 0.04f),
                                spotColor = HydroInk.copy(alpha = 0.08f)
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = label(option),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun StepHeader(step: Int, total: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        HydroBackButton(onClick = onBack)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(HydroTrack, RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(step.toFloat() / total)
                        .height(6.dp)
                        .background(HydroPrimary, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
fun ScreenTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    large: Boolean = false
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = if (large) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium,
            color = HydroInk
        )
        if (subtitle != null) {
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = HydroInkMuted)
        }
    }
}

@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = HydroOutline)
        Text(
            text = stringResource(R.string.or),
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = HydroInkMuted
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = HydroOutline)
    }
}
