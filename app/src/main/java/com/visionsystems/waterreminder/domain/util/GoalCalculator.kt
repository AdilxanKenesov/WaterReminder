package com.visionsystems.waterreminder.domain.util

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.domain.module.GoalTemplate
import kotlin.math.roundToInt

object GoalCalculator {
    const val DEFAULT_GOAL_ML = 2000
    const val MIN_GOAL_ML = 1000
    const val MAX_GOAL_ML = 5000
    private const val STEP_ML = 50
    private const val SENIOR_AGE = 65
    private const val SENIOR_FACTOR = 0.9

    fun recommended(weightKg: Int, gender: Gender, activityLevel: ActivityLevel, age: Int): Int {
        if (weightKg <= 0) return DEFAULT_GOAL_ML
        val perKg = when (gender) {
            Gender.MALE -> 35
            Gender.FEMALE -> 31
            Gender.OTHER -> 33
        }
        val activityBonus = when (activityLevel) {
            ActivityLevel.LOW -> 0
            ActivityLevel.MODERATE -> 350
            ActivityLevel.HIGH -> 700
        }
        val ageFactor = if (age >= SENIOR_AGE) SENIOR_FACTOR else 1.0
        val raw = ((weightKg * perKg + activityBonus) * ageFactor).roundToInt()
        return roundToStep(raw).coerceIn(MIN_GOAL_ML, MAX_GOAL_ML)
    }

    fun fromTemplate(template: GoalTemplate, cupMl: Int): Int =
        roundToStep(template.glasses * cupMl).coerceIn(MIN_GOAL_ML, MAX_GOAL_ML)

    private fun roundToStep(value: Int): Int = ((value + STEP_ML / 2) / STEP_ML) * STEP_ML
}
