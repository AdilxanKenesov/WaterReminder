package com.visionsystems.waterreminder.domain.usecase.insights

import com.visionsystems.waterreminder.domain.module.InsightsUiData
import kotlinx.coroutines.flow.Flow

interface InsightsUseCase {
    fun observeInsights(days: Int): Flow<InsightsUiData>
}
