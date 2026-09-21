package com.visionsystems.waterreminder.domain.module

data class ProfileOverviewUiData(
    val profile: UserProfileUiData?,
    val displayName: String,
    val email: String,
    val photo: String?,
    val goalMl: Int,
    val recommendedMl: Int,
    val unit: WaterUnit
)
