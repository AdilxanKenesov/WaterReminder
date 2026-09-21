package com.visionsystems.waterreminder.domain.util

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.domain.module.GoalTemplate
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalCalculatorTest {

    @Test
    fun `male with moderate activity gets weight based goal rounded to 50`() {
        assertEquals(2800, GoalCalculator.recommended(70, Gender.MALE, ActivityLevel.MODERATE, 30))
    }

    @Test
    fun `female goal uses lower coefficient`() {
        assertEquals(1850, GoalCalculator.recommended(60, Gender.FEMALE, ActivityLevel.LOW, 25))
    }

    @Test
    fun `senior goal is reduced`() {
        assertEquals(2200, GoalCalculator.recommended(70, Gender.MALE, ActivityLevel.LOW, 70))
    }

    @Test
    fun `unknown weight falls back to default`() {
        assertEquals(GoalCalculator.DEFAULT_GOAL_ML, GoalCalculator.recommended(0, Gender.OTHER, ActivityLevel.LOW, 30))
    }

    @Test
    fun `goal is clamped to allowed range`() {
        assertEquals(GoalCalculator.MAX_GOAL_ML, GoalCalculator.recommended(200, Gender.MALE, ActivityLevel.HIGH, 30))
        assertEquals(GoalCalculator.MIN_GOAL_ML, GoalCalculator.recommended(20, Gender.FEMALE, ActivityLevel.LOW, 30))
    }

    @Test
    fun `template multiplies glasses by cup size`() {
        assertEquals(2500, GoalCalculator.fromTemplate(GoalTemplate.SUMMER, 250))
        assertEquals(1000, GoalCalculator.fromTemplate(GoalTemplate.CHILD, 200))
    }
}
