package com.visionsystems.waterreminder.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import com.visionsystems.waterreminder.QuickAddActivity

class QuickAddTileService : TileService() {

    override fun onClick() {
        if (isLocked) unlockAndRun { openQuickAdd() } else openQuickAdd()
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun openQuickAdd() {
        val intent = Intent(this, QuickAddActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE))
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
