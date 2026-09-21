package com.visionsystems.waterreminder.domain.util

import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 9, 22)

    private fun day(daysAgo: Long, completed: Boolean) = DayTotalUiData(
        dayKey = today.minusDays(daysAgo).toDayKey(),
        goalMl = 2000,
        consumedMl = if (completed) 2000 else 500,
        drinkCount = 3
    )

    @Test
    fun `streak counts today when completed`() {
        val days = listOf(day(2, true), day(1, true), day(0, true))
        assertEquals(3, StreakCalculator.current(days, today))
    }

    @Test
    fun `unfinished today does not break yesterday streak`() {
        val days = listOf(day(2, true), day(1, true), day(0, false))
        assertEquals(2, StreakCalculator.current(days, today))
    }

    @Test
    fun `gap breaks the streak`() {
        val days = listOf(day(4, true), day(3, true), day(1, true), day(0, true))
        assertEquals(2, StreakCalculator.current(days, today))
    }

    @Test
    fun `no completed days gives zero`() {
        assertEquals(0, StreakCalculator.current(listOf(day(0, false)), today))
    }

    @Test
    fun `best streak finds the longest run across month boundary`() {
        val start = LocalDate.of(2026, 8, 29)
        val days = (0L..4L).map { offset ->
            DayTotalUiData(start.plusDays(offset).toDayKey(), 2000, 2100, 4)
        } + day(0, true)
        assertEquals(5, StreakCalculator.best(days))
    }
}
