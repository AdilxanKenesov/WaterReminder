package com.visionsystems.waterreminder.domain.repository

interface ReminderScheduler {
    suspend fun ensureScheduled()
    suspend fun rescheduleAll()
    suspend fun scheduleNextReminder()
    suspend fun scheduleNextStreakCheck()
    fun scheduleNextMidnight()
    fun snooze(minutes: Long)
    fun cancelAll()
}
