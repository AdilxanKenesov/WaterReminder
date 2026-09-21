package com.visionsystems.waterreminder.data.source.local.notification

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.visionsystems.waterreminder.R

object NotificationChannels {

    const val REMINDERS = "channel_reminders"
    const val PROGRESS = "channel_progress"
    const val ACHIEVEMENTS = "channel_achievements"
    private const val GROUP = "group_hydro"
    private const val LEGACY_CHANNEL = "channel_playback"

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService<NotificationManager>() ?: return
        manager.deleteNotificationChannel(LEGACY_CHANNEL)
        manager.createNotificationChannelGroup(
            NotificationChannelGroup(GROUP, context.getString(R.string.channel_group_name))
        )
        manager.createNotificationChannels(
            listOf(
                channel(context, REMINDERS, R.string.channel_reminders_name, R.string.channel_reminders_description, NotificationManager.IMPORTANCE_HIGH),
                channel(context, PROGRESS, R.string.channel_progress_name, R.string.channel_progress_description, NotificationManager.IMPORTANCE_DEFAULT),
                channel(context, ACHIEVEMENTS, R.string.channel_achievements_name, R.string.channel_achievements_description, NotificationManager.IMPORTANCE_LOW)
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun channel(context: Context, id: String, name: Int, description: Int, importance: Int) =
        NotificationChannel(id, context.getString(name), importance).apply {
            this.description = context.getString(description)
            group = GROUP
        }
}
