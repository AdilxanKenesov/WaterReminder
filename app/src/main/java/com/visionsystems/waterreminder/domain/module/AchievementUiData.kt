package com.visionsystems.waterreminder.domain.module

data class AchievementUiData(
    val type: AchievementType,
    val unlockedAt: Long?
) {
    val isUnlocked: Boolean get() = unlockedAt != null
}
