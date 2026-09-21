package com.visionsystems.waterreminder.domain.module

data class ReminderConfigUiData(
    val enabled: Boolean = true,
    val intervalMinutes: Int? = null,
    val soundEnabled: Boolean = true,
    val goalNotify: Boolean = true,
    val streakNotify: Boolean = true,
    val achievementNotify: Boolean = true
)
