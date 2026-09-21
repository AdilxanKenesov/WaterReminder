package com.visionsystems.waterreminder.data.repository_impl

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.data.receiver.NotificationActionReceiver
import com.visionsystems.waterreminder.data.source.local.notification.NotificationChannels
import com.visionsystems.waterreminder.data.source.local.pref.SharedManager
import com.visionsystems.waterreminder.domain.module.AchievementType
import com.visionsystems.waterreminder.domain.repository.NotificationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val sharedManager: SharedManager
) : NotificationRepository {

    private val manager = NotificationManagerCompat.from(context)

    private val localized: Context
        get() {
            val tag = AppCompatDelegate.getApplicationLocales().toLanguageTags().ifEmpty { sharedManager.language } ?: return context
            val configuration = Configuration(context.resources.configuration).apply {
                setLocales(LocaleList.forLanguageTags(tag))
            }
            return context.createConfigurationContext(configuration)
        }

    override fun canNotify(): Boolean {
        if (!manager.areNotificationsEnabled()) return false
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    override fun showReminder(consumedMl: Int, goalMl: Int, cupMl: Int, silent: Boolean) {
        val notification = builder(NotificationChannels.REMINDERS)
            .setContentTitle(localized.getString(R.string.notification_reminder_title))
            .setContentText(localized.getString(R.string.notification_reminder_text, consumedMl, goalMl))
            .setProgress(goalMl, consumedMl.coerceAtMost(goalMl), false)
            .setSilent(silent)
            .addAction(0, localized.getString(R.string.notification_action_add, cupMl), addWaterIntent(cupMl, REMINDER_ID))
            .addAction(0, localized.getString(R.string.notification_action_later), snoozeIntent())
            .build()
        notify(REMINDER_ID, notification)
    }

    override fun showGoalReached(goalMl: Int) {
        val notification = builder(NotificationChannels.PROGRESS)
            .setContentTitle(localized.getString(R.string.notification_goal_title))
            .setContentText(localized.getString(R.string.notification_goal_text, goalMl))
            .build()
        notify(GOAL_ID, notification)
    }

    override fun showStreakAtRisk(streak: Int, remainingMl: Int, cupMl: Int) {
        val notification = builder(NotificationChannels.PROGRESS)
            .setContentTitle(localized.getString(R.string.notification_streak_title, streak))
            .setContentText(localized.getString(R.string.notification_streak_text, remainingMl))
            .addAction(0, localized.getString(R.string.notification_action_add, cupMl), addWaterIntent(cupMl, STREAK_ID))
            .build()
        notify(STREAK_ID, notification)
    }

    override fun showAchievement(type: AchievementType) {
        val notification = builder(NotificationChannels.ACHIEVEMENTS)
            .setContentTitle(localized.getString(R.string.notification_achievement_title))
            .setContentText(localized.getString(type.titleRes()))
            .build()
        notify(ACHIEVEMENT_BASE_ID + type.ordinal, notification)
    }

    override fun cancelReminder() {
        manager.cancel(REMINDER_ID)
        manager.cancel(STREAK_ID)
    }

    private fun builder(channelId: String): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_drop)
            .setColor(ContextCompat.getColor(context, R.color.hydro_primary))
            .setAutoCancel(true)
            .setContentIntent(openAppIntent())

    @SuppressLint("MissingPermission")
    private fun notify(id: Int, notification: android.app.Notification) {
        if (canNotify()) manager.notify(id, notification)
    }

    private fun openAppIntent(): PendingIntent? {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        } ?: return null
        return PendingIntent.getActivity(context, REQUEST_OPEN_APP, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun addWaterIntent(amountMl: Int, sourceId: Int): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
            .setAction(NotificationActionReceiver.ACTION_ADD_WATER)
            .putExtra(NotificationActionReceiver.EXTRA_AMOUNT_ML, amountMl)
        return PendingIntent.getBroadcast(context, sourceId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun snoozeIntent(): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
            .setAction(NotificationActionReceiver.ACTION_SNOOZE)
        return PendingIntent.getBroadcast(context, REQUEST_SNOOZE, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun AchievementType.titleRes(): Int = when (this) {
        AchievementType.FIRST_GLASS -> R.string.achievement_first_glass
        AchievementType.FIRST_GOAL -> R.string.achievement_first_goal
        AchievementType.STREAK_3 -> R.string.achievement_streak_3
        AchievementType.STREAK_7 -> R.string.achievement_streak_7
        AchievementType.STREAK_30 -> R.string.achievement_streak_30
        AchievementType.TOTAL_10_LITERS -> R.string.achievement_total_10_liters
        AchievementType.TOTAL_100_LITERS -> R.string.achievement_total_100_liters
    }

    private companion object {
        const val REMINDER_ID = 1001
        const val GOAL_ID = 1002
        const val STREAK_ID = 1003
        const val ACHIEVEMENT_BASE_ID = 1100
        const val REQUEST_OPEN_APP = 2001
        const val REQUEST_SNOOZE = 2002
    }
}
