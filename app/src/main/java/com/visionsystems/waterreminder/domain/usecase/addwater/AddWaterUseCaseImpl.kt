package com.visionsystems.waterreminder.domain.usecase.addwater

import com.visionsystems.waterreminder.domain.module.AddWaterUiData
import com.visionsystems.waterreminder.domain.module.DrinkResultUiData
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.usecase.drink.DrinkUseCase
import com.visionsystems.waterreminder.domain.util.ReminderPreview
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddWaterUseCaseImpl @Inject constructor(
    private val cupRepository: CupRepository,
    private val settingsRepository: SettingsRepository,
    private val drinkUseCase: DrinkUseCase
) : AddWaterUseCase {

    override suspend fun loadDefaults(): AddWaterUiData {
        val cups = cupRepository.observeCups().first()
        return AddWaterUiData(
            selectedCupMl = ReminderPreview.selectedCupMl(cups),
            cups = cups,
            unit = settingsRepository.getUnit().value
        )
    }

    override suspend fun addDrink(amountMl: Int): DrinkResultUiData = drinkUseCase.addDrink(amountMl)
}
