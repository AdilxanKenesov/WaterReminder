package com.visionsystems.waterreminder.presenter.screens.addwater

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.presenter.ui.components.AddWaterPanel
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun AddWaterScreen() {
    val viewModel: AddWaterContract.AddWaterViewModel = hiltViewModel<AddWaterViewModel>()
    val uiState = viewModel.collectAsState()
    AddWaterContent(uiState = uiState.value, onEventDispatcher = viewModel::onEventDispatcher)
}

@Composable
private fun AddWaterContent(
    uiState: AddWaterContract.AddWaterUiState,
    onEventDispatcher: (AddWaterContract.AddWaterEvent) -> Unit
) {
    AddWaterPanel(
        amountMl = uiState.amountMl,
        presets = uiState.presets,
        unit = uiState.unit,
        isSaving = uiState.isSaving,
        onAmountChange = { onEventDispatcher(AddWaterContract.AddWaterEvent.AmountChanged(it)) },
        onAdd = { onEventDispatcher(AddWaterContract.AddWaterEvent.AddClicked) },
        modifier = Modifier
            .background(Color.White)
            .padding(start = 22.dp, end = 22.dp, bottom = 24.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun AddWaterPreview() {
    HydroTheme {
        AddWaterContent(
            uiState = AddWaterContract.AddWaterUiState(amountMl = 300, presets = listOf(100, 200, 250, 500)),
            onEventDispatcher = {}
        )
    }
}
