package com.visionsystems.waterreminder.presenter.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.navigation.MainTab
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroCoral
import com.visionsystems.waterreminder.presenter.ui.theme.HydroInkMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary

private data class BarItem(val tab: MainTab, val label: Int, val icon: Int, val selectedIcon: Int)

private val BarItems = listOf(
    BarItem(MainTab.HOME, R.string.tab_home, R.drawable.ic_home, R.drawable.ic_home_fill),
    BarItem(MainTab.INSIGHTS, R.string.tab_insights, R.drawable.ic_insights, R.drawable.ic_insights_fill),
    BarItem(MainTab.REMINDERS, R.string.tab_reminders, R.drawable.ic_bell, R.drawable.ic_bell_fill),
    BarItem(MainTab.PROFILE, R.string.tab_profile, R.drawable.ic_person, R.drawable.ic_person_fill)
)

@Composable
fun HydroBottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 26.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(70.dp)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BarItems.take(2).forEach { BarButton(it, it.tab == selected) { onSelect(it.tab) } }
                Spacer(Modifier.width(62.dp))
                BarItems.drop(2).forEach { BarButton(it, it.tab == selected) { onSelect(it.tab) } }
            }
        }
        Surface(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(62.dp),
            shape = CircleShape,
            color = HydroCoral,
            contentColor = Color.White,
            border = BorderStroke(4.dp, HydroBackground),
            shadowElevation = 10.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(R.drawable.ic_add), contentDescription = stringResource(R.string.add_water), modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
private fun BarButton(item: BarItem, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) HydroPrimary else HydroInkMuted
    Column(
        modifier = Modifier
            .width(68.dp)
            .height(56.dp)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically)
    ) {
        Icon(
            painter = painterResource(if (selected) item.selectedIcon else item.icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = stringResource(item.label),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = color
        )
    }
}
