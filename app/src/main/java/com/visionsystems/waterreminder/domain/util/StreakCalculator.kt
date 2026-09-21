package com.visionsystems.waterreminder.domain.util

import com.visionsystems.waterreminder.domain.module.DayTotalUiData
import java.time.LocalDate

object StreakCalculator {

    fun current(days: List<DayTotalUiData>, today: LocalDate): Int {
        val completed = days.filter { it.isCompleted }.mapTo(HashSet()) { it.dayKey }
        var cursor = if (today.toDayKey() in completed) today else today.minusDays(1)
        var count = 0
        while (cursor.toDayKey() in completed) {
            count++
            cursor = cursor.minusDays(1)
        }
        return count
    }

    fun best(days: List<DayTotalUiData>): Int {
        val completed = days.filter { it.isCompleted }.map { it.dayKey.toLocalDate() }.sorted()
        var best = 0
        var run = 0
        var previous: LocalDate? = null
        for (day in completed) {
            run = if (previous != null && previous.plusDays(1) == day) run + 1 else 1
            if (run > best) best = run
            previous = day
        }
        return best
    }
}
