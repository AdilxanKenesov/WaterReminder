package com.visionsystems.waterreminder.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class ReminderPlannerTest {

    private val wake = LocalTime.of(7, 0)
    private val sleep = LocalTime.of(23, 0)

    @Test
    fun `awake minutes handle sleep after midnight`() {
        assertEquals(16 * 60, ReminderPlanner.awakeMinutes(wake, sleep))
        assertEquals(18 * 60, ReminderPlanner.awakeMinutes(LocalTime.of(8, 0), LocalTime.of(2, 0)))
    }

    @Test
    fun `interval is spread across awake time and clamped`() {
        assertEquals(120, ReminderPlanner.intervalMinutes(wake, sleep, 2000, 250, null))
        assertEquals(ReminderPlanner.MIN_INTERVAL_MINUTES, ReminderPlanner.intervalMinutes(wake, sleep, 5000, 50, null))
        assertEquals(90, ReminderPlanner.intervalMinutes(wake, sleep, 2000, 250, 90))
    }

    @Test
    fun `next reminder is the next slot after now`() {
        val now = LocalDateTime.of(2026, 9, 22, 10, 30)
        assertEquals(LocalDateTime.of(2026, 9, 22, 11, 0), ReminderPlanner.nextReminder(now, wake, sleep, 120))
    }

    @Test
    fun `after sleep time next reminder moves to tomorrow`() {
        val now = LocalDateTime.of(2026, 9, 22, 23, 30)
        assertEquals(LocalDateTime.of(2026, 9, 23, 9, 0), ReminderPlanner.nextReminder(now, wake, sleep, 120))
    }

    @Test
    fun `window crossing midnight keeps reminding after midnight`() {
        val lateSleep = LocalTime.of(2, 0)
        val now = LocalDateTime.of(2026, 9, 23, 0, 30)
        assertEquals(LocalDateTime.of(2026, 9, 23, 1, 0), ReminderPlanner.nextReminder(now, LocalTime.of(9, 0), lateSleep, 120))
    }

    @Test
    fun `awake check respects window`() {
        assertTrue(ReminderPlanner.isAwake(LocalDateTime.of(2026, 9, 22, 12, 0), wake, sleep))
        assertFalse(ReminderPlanner.isAwake(LocalDateTime.of(2026, 9, 22, 3, 0), wake, sleep))
        assertTrue(ReminderPlanner.isAwake(LocalDateTime.of(2026, 9, 23, 1, 0), LocalTime.of(9, 0), LocalTime.of(2, 0)))
    }

    @Test
    fun `streak check runs two hours before sleep`() {
        val now = LocalDateTime.of(2026, 9, 22, 22, 0)
        assertEquals(LocalDateTime.of(2026, 9, 23, 21, 0), ReminderPlanner.nextStreakCheck(now, sleep))
        assertEquals(LocalDateTime.of(2026, 9, 22, 21, 0), ReminderPlanner.nextStreakCheck(now.withHour(20), sleep))
    }
}
