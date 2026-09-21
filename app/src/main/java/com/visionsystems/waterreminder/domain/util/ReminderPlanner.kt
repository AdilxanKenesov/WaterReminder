package com.visionsystems.waterreminder.domain.util

import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.ceil

object ReminderPlanner {
    const val MIN_INTERVAL_MINUTES = 30
    const val MAX_INTERVAL_MINUTES = 240
    const val STREAK_CHECK_BEFORE_SLEEP_MINUTES = 120L
    private const val MINUTES_IN_DAY = 24 * 60

    fun awakeMinutes(wake: LocalTime, sleep: LocalTime): Int {
        val wakeMinute = wake.toMinuteOfDay()
        val sleepMinute = sleep.toMinuteOfDay()
        return if (sleepMinute > wakeMinute) sleepMinute - wakeMinute else sleepMinute + MINUTES_IN_DAY - wakeMinute
    }

    fun intervalMinutes(wake: LocalTime, sleep: LocalTime, goalMl: Int, cupMl: Int, overrideMinutes: Int?): Int {
        if (overrideMinutes != null) return overrideMinutes.coerceIn(MIN_INTERVAL_MINUTES, MAX_INTERVAL_MINUTES)
        val cups = ceil(goalMl.toDouble() / cupMl.coerceAtLeast(1)).toInt().coerceAtLeast(1)
        return (awakeMinutes(wake, sleep) / cups).coerceIn(MIN_INTERVAL_MINUTES, MAX_INTERVAL_MINUTES)
    }

    fun isAwake(now: LocalDateTime, wake: LocalTime, sleep: LocalTime): Boolean {
        val awake = awakeMinutes(wake, sleep).toLong()
        return (-1L..0L).any { offset ->
            val start = now.toLocalDate().plusDays(offset).atTime(wake)
            !now.isBefore(start) && now.isBefore(start.plusMinutes(awake))
        }
    }

    fun nextReminder(now: LocalDateTime, wake: LocalTime, sleep: LocalTime, intervalMinutes: Int): LocalDateTime {
        val interval = intervalMinutes.coerceAtLeast(MIN_INTERVAL_MINUTES).toLong()
        val awake = awakeMinutes(wake, sleep).toLong()
        for (offset in -1L..1L) {
            val start = now.toLocalDate().plusDays(offset).atTime(wake)
            val end = start.plusMinutes(awake)
            var slot = start.plusMinutes(interval)
            while (slot.isBefore(end)) {
                if (slot.isAfter(now)) return slot
                slot = slot.plusMinutes(interval)
            }
        }
        return now.toLocalDate().plusDays(1).atTime(wake).plusMinutes(interval)
    }

    fun slots(wake: LocalTime, sleep: LocalTime, intervalMinutes: Int): List<LocalTime> {
        val interval = intervalMinutes.coerceAtLeast(MIN_INTERVAL_MINUTES)
        val awake = awakeMinutes(wake, sleep)
        return generateSequence(interval) { it + interval }
            .takeWhile { it < awake }
            .map { wake.plusMinutes(it.toLong()) }
            .toList()
    }

    fun nextStreakCheck(now: LocalDateTime, sleep: LocalTime): LocalDateTime =
        nextOccurrence(now, sleep.minusMinutes(STREAK_CHECK_BEFORE_SLEEP_MINUTES))

    fun nextMidnight(now: LocalDateTime): LocalDateTime = now.toLocalDate().plusDays(1).atStartOfDay()

    private fun nextOccurrence(now: LocalDateTime, time: LocalTime): LocalDateTime {
        val today = now.toLocalDate().atTime(time)
        return if (today.isAfter(now)) today else today.plusDays(1)
    }
}
