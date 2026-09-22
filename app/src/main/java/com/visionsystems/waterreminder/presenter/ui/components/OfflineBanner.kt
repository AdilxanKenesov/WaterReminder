package com.visionsystems.waterreminder.presenter.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoralInk
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoralTint
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInk

@Composable
fun OfflineBanner(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(visible = visible, modifier = modifier, enter = expandVertically(), exit = shrinkVertically()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(HydroCoralTint, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(painter = painterResource(R.drawable.ic_wifi_off), contentDescription = null, tint = HydroCoralInk, modifier = Modifier.size(20.dp))
            Text(text = stringResource(R.string.offline_message), style = MaterialTheme.typography.bodySmall, color = HydroInk)
        }
    }
}
