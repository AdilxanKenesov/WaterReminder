package com.visionsystems.waterreminder.domain.usecase.drink

import com.visionsystems.waterreminder.domain.module.DrinkResultUiData
import com.visionsystems.waterreminder.domain.repository.AchievementRepository
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.DrinkRepository
import com.visionsystems.waterreminder.domain.repository.GoalRepository
import com.visionsystems.waterreminder.domain.repository.NotificationRepository
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import com.visionsystems.waterreminder.domain.repository.SettingsRepository
import com.visionsystems.waterreminder.domain.repository.WidgetRepository
import javax.inject.Inject

class DrinkUseCaseImpl @Inject constructor(
    private val drinkRepository: DrinkRepository,
    private val goalRepository: GoalRepository,
    private val cupRepository: CupRepository,
    private val achievementRepository: AchievementRepository,
    private val reminderRepository: ReminderRepository,
    private val notificationRepository: NotificationRepository,
    private val settingsRepository: SettingsRepository,
    private val widgetRepository: WidgetRepository,
    private val currentUser: CurrentUserProvider
) : DrinkUseCase {

    override suspend fun addDrink(amountMl: Int): DrinkResultUiData {
        goalRepository.ensureGoalForToday()
        val before = drinkRepository.getToday()
        val entryId = drinkRepository.addDrink(amountMl)
        val after = drinkRepository.getToday()
        val uid = currentUser.currentUid.value
        val config = reminderRepository.getConfig()

        val goalJustReached = !before.isCompleted && after.isCompleted && !settingsRepository.wasGoalNotified(uid, after.dayKey)
        if (after.isCompleted) notificationRepository.cancelReminder()
        if (goalJustReached) {
            settingsRepository.markGoalNotified(uid, after.dayKey)
            if (config.goalNotify) notificationRepository.showGoalReached(after.goalMl)
        }

        val newAchievements = achievementRepository.evaluate()
        if (config.achievementNotify) newAchievements.forEach { notificationRepository.showAchievement(it) }

        widgetRepository.refresh()
        return DrinkResultUiData(
            entryId = entryId,
            progress = after,
            goalJustReached = goalJustReached,
            newAchievements = newAchievements
        )
    }

    override suspend fun addSelectedCup(): DrinkResultUiData = addDrink(cupRepository.getSelectedCupMl())

    override suspend fun undoDrink(entryId: Long) {
        drinkRepository.deleteDrink(entryId)
        widgetRepository.refresh()
    }
}
