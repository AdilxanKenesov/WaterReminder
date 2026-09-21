package com.visionsystems.waterreminder.presenter.screens.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.visionsystems.waterreminder.navigation.MainTab
import com.visionsystems.waterreminder.presenter.screens.home.HomeScreen
import com.visionsystems.waterreminder.presenter.screens.insights.InsightsScreen
import com.visionsystems.waterreminder.presenter.screens.profile.ProfileScreen
import com.visionsystems.waterreminder.presenter.screens.reminders.RemindersScreen
import com.visionsystems.waterreminder.presenter.ui.components.HydroBottomBar
import com.visionsystems.waterreminder.presenter.ui.theme.HydroBackground
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MainScreen(selectedTab: MainTab) {
    val viewModel: MainContract.MainViewModel = hiltViewModel<MainViewModel>()
    val uiState = viewModel.collectAsState()
    NotificationPermissionRequest()
    BackHandler(enabled = selectedTab != MainTab.HOME) {
        viewModel.onEventDispatcher(MainContract.MainEvent.TabSelected(MainTab.HOME))
    }
    MainContent(
        uiState = uiState.value,
        selectedTab = selectedTab,
        onEventDispatcher = viewModel::onEventDispatcher
    ) { tab, contentPadding ->
        when (tab) {
            MainTab.HOME -> HomeScreen(contentPadding)
            MainTab.INSIGHTS -> InsightsScreen(contentPadding)
            MainTab.REMINDERS -> RemindersScreen(contentPadding)
            MainTab.PROFILE -> ProfileScreen(contentPadding)
        }
    }
}

@Composable
private fun MainContent(
    uiState: MainContract.MainUiState,
    selectedTab: MainTab,
    onEventDispatcher: (MainContract.MainEvent) -> Unit,
    tabContent: @Composable (MainTab, PaddingValues) -> Unit
) {
    val stateHolder = rememberSaveableStateHolder()
    Scaffold(
        containerColor = HydroBackground,
        bottomBar = {
            HydroBottomBar(
                selected = selectedTab,
                onSelect = { onEventDispatcher(MainContract.MainEvent.TabSelected(it)) },
                onAddClick = { onEventDispatcher(MainContract.MainEvent.AddWaterClicked) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            stateHolder.SaveableStateProvider(selectedTab.name) {
                tabContent(selectedTab, innerPadding)
            }
        }
    }
}

@Composable
private fun NotificationPermissionRequest() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || LocalInspectionMode.current) return
    val context = LocalContext.current
    var asked by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(asked) {
        if (asked) return@LaunchedEffect
        asked = true
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!granted) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MainPreview() {
    HydroTheme {
        MainContent(
            uiState = MainContract.MainUiState(),
            selectedTab = MainTab.HOME,
            onEventDispatcher = {},
            tabContent = { _, _ -> }
        )
    }
}
