package com.visionsystems.waterreminder.domain.module

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}

enum class ActivityLevel {
    LOW,
    MODERATE,
    HIGH
}

enum class WaterUnit {
    ML,
    OZ
}

enum class GoalTemplate(val glasses: Int) {
    SUMMER(10),
    SPORTY(7),
    SNOW_DAY(5),
    CHILD(4)
}

enum class AchievementType(val key: String) {
    FIRST_GLASS("first_glass"),
    FIRST_GOAL("first_goal"),
    STREAK_3("streak_3"),
    STREAK_7("streak_7"),
    STREAK_30("streak_30"),
    TOTAL_10_LITERS("total_10_liters"),
    TOTAL_100_LITERS("total_100_liters");

    companion object {
        fun fromKey(key: String): AchievementType? = entries.firstOrNull { it.key == key }
    }
}
