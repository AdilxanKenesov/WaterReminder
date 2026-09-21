package com.visionsystems.waterreminder.presenter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.WaterUnit
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryTint
import com.visionsystems.waterreminder.presenter.ui.util.label
import com.visionsystems.waterreminder.presenter.ui.util.toVolume
import com.visionsystems.waterreminder.presenter.ui.util.toVolumeText

val AddWaterRange = 50..1000
const val ADD_WATER_STEP = 50

@Composable
fun AddWaterPanel(
    amountMl: Int,
    presets: List<Int>,
    unit: WaterUnit,
    isSaving: Boolean,
    onAmountChange: (Int) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(text = stringResource(R.string.add_water), style = MaterialTheme.typography.titleLarge, color = HydroInk)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon = R.drawable.ic_drop, background = HydroPrimaryTint, tint = HydroPrimary, size = 64.dp, radius = 22.dp, iconSize = 32.dp)
            Text(
                text = buildAnnotatedString {
                    append(amountMl.toVolume(unit))
                    withStyle(SpanStyle(fontSize = 20.sp, color = HydroInkMuted)) { append(" ${unit.label}") }
                },
                style = MaterialTheme.typography.displayLarge,
                color = HydroInk
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            HydroSlider(value = amountMl, range = AddWaterRange, step = ADD_WATER_STEP, onValueChange = onAmountChange)
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = AddWaterRange.first.toVolumeText(unit),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 12.sp,
                    color = HydroInkMuted,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = AddWaterRange.last.toVolumeText(unit),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 12.sp,
                    color = HydroInkMuted
                )
            }
        }
        if (presets.isNotEmpty()) {
            HydroChipGroup(
                options = presets,
                selected = amountMl,
                label = { it.toVolume(unit) },
                onSelect = onAmountChange
            )
        }
        HydroPrimaryButton(
            text = stringResource(R.string.add_amount, amountMl.toVolumeText(unit)),
            loading = isSaving,
            onClick = onAdd
        )
    }
}
