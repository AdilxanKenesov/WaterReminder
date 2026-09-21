package com.visionsystems.waterreminder

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.visionsystems.waterreminder.navigation.AppNavHost
import com.visionsystems.waterreminder.navigation.AppNavigationHandler
import com.visionsystems.waterreminder.presenter.start.StartViewModel
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigationHandler: AppNavigationHandler

    private val startViewModel: StartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { startViewModel.startRoute.value == null }
        enableEdgeToEdge()
        setContent {
            val startRoute by startViewModel.startRoute.collectAsStateWithLifecycle()
            HydroTheme {
                startRoute?.let { AppNavHost(handler = navigationHandler, startRoute = it) }
            }
        }
    }
}
