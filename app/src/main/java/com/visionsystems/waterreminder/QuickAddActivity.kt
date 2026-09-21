package com.visionsystems.waterreminder

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.visionsystems.waterreminder.presenter.screens.quickadd.QuickAddScreen
import com.visionsystems.waterreminder.presenter.ui.theme.HydroTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HydroTheme {
                QuickAddScreen(
                    onClose = ::finish,
                    onOpenApp = {
                        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    }
                )
            }
        }
    }
}
