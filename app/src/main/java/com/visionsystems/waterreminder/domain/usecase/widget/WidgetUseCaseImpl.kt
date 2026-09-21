package com.visionsystems.waterreminder.domain.usecase.widget

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.domain.module.WidgetUiData
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.usecase.common.StartResolver
import com.visionsystems.waterreminder.domain.usecase.drink.DrinkUseCase
import javax.inject.Inject

class WidgetUseCaseImpl @Inject constructor(
    private val startResolver: StartResolver,
    private val drinkRepository: DrinkRepository,
    private val cupRepository: CupRepository,
    private val settingsRepository: SettingsRepository,
    private val drinkUseCase: DrinkUseCase
) : WidgetUseCase {

    override suspend fun load(): WidgetUiData {
        val ready = startResolver.resolve() == AppStart.MAIN
        val today = if (ready) drinkRepository.getToday() else null
        return WidgetUiData(
            isReady = ready,
            consumedMl = today?.consumedMl ?: 0,
            goalMl = today?.goalMl ?: 0,
            cupMl = if (ready) cupRepository.getSelectedCupMl() else 0,
            unit = settingsRepository.getUnit().value
        )
    }

    override suspend fun addDrink(amountMl: Int) {
        if (startResolver.resolve() != AppStart.MAIN) return
        drinkUseCase.addDrink(amountMl)
    }
}
