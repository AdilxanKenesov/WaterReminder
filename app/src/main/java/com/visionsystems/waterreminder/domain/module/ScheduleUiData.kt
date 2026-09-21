package com.visionsystems.waterreminder.domain.module

import java.time.LocalTime

data class SchedulePlanUiData(
    val intervalMinutes: Int,
    val slots: List<LocalTime>
)

data class AddWaterUiData(
    val selectedCupMl: Int,
    val cups: List<CupSizeUiData>,
    val unit: WaterUnit
)

data class EditProfileUiData(
    val profile: UserProfileUiData,
    val photo: String?
)
