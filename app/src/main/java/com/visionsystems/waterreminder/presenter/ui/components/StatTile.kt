package com.visionsystems.waterreminder.presenter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted

@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    HydroCard(modifier = modifier, radius = 20.dp, contentPadding = PaddingValues(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = HydroInkMuted)
            Text(
                text = buildAnnotatedString {
                    append(value)
                    if (suffix != null) {
                        withStyle(SpanStyle(fontSize = 15.sp, color = HydroInkMuted)) { append(" $suffix") }
                    }
                },
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 26.sp,
                color = HydroInk
            )
        }
    }
}
