package com.visionsystems.waterreminder.presenter.screens.quickadd

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.presenter.ui.components.AddWaterPanel
import com.visionsystems.waterreminder.presenter.ui.theme.HydroScrim
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun QuickAddScreen(onClose: () -> Unit, onOpenApp: () -> Unit) {
    val viewModel: QuickAddContract.QuickAddViewModel = hiltViewModel<QuickAddViewModel>()
    val uiState = viewModel.collectAsState()
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            QuickAddContract.SideEffect.Close -> onClose()
            QuickAddContract.SideEffect.OpenApp -> onOpenApp()
        }
    }
    BackHandler { viewModel.onEventDispatcher(QuickAddContract.QuickAddEvent.DismissClicked) }
    QuickAddContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun QuickAddContent(
    uiState: QuickAddContract.QuickAddUiState,
    onEventDispatcher: (QuickAddContract.QuickAddEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroScrim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onEventDispatcher(QuickAddContract.QuickAddEvent.DismissClicked) }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = !uiState.isLoading,
            enter = slideInVertically(initialOffsetY = { it })
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {}),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White
            ) {
                AddWaterPanel(
                    amountMl = uiState.amountMl,
                    presets = uiState.presets,
                    unit = uiState.unit,
                    isSaving = uiState.isSaving,
                    onAmountChange = { onEventDispatcher(QuickAddContract.QuickAddEvent.AmountChanged(it)) },
                    onAdd = { onEventDispatcher(QuickAddContract.QuickAddEvent.AddClicked) },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 24.dp)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun QuickAddPreview() {
    HydroTheme {
        QuickAddContent(
            uiState = QuickAddContract.QuickAddUiState(isLoading = false, amountMl = 300, presets = listOf(100, 200, 250, 500)),
            onEventDispatcher = {}
        )
    }
}
