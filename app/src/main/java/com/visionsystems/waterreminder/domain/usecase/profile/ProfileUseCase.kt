package com.visionsystems.waterreminder.domain.usecase.profile

import com.visionsystems.waterreminder.domain.module.ProfileOverviewUiData
import com.visionsystems.waterreminder.domain.module.WaterUnit
import kotlinx.coroutines.flow.Flow

interface ProfileUseCase {
    fun observeProfile(): Flow<ProfileOverviewUiData>
    fun setUnit(unit: WaterUnit)
    fun setLanguage(tag: String)
    fun signOut()
    suspend fun deleteAccount(): Result<Unit>
}
