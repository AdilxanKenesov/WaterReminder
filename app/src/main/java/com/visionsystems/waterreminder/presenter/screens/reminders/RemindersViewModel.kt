package com.visionsystems.waterreminder.presenter.screens.reminders

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.module.ReminderConfigUiData
import com.visionsystems.waterreminder.domain.usecase.reminders.RemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val direction: RemindersContract.Direction,
    private val remindersUseCase: RemindersUseCase
) : ViewModel(), RemindersContract.RemindersViewModel {

    override val container = orbitContainer<RemindersContract.RemindersUiState, RemindersContract.SideEffect>(RemindersContract.RemindersUiState())

    init {
        intent {
            remindersUseCase.observeReminders().collect { data ->
                reduce {
                    state.copy(
                        isLoading = false,
                        config = data.config,
                        wakeTime = data.wakeTime,
                        sleepTime = data.sleepTime,
                        nextReminder = data.nextReminder,
                        slots = data.slots,
                        intervalMinutes = data.intervalMinutes,
                        goalCompleted = data.goalCompleted,
                        canNotify = data.canNotify
                    )
                }
            }
        }
    }

    override fun onEventDispatcher(event: RemindersContract.RemindersEvent) {
        when (event) {
            is RemindersContract.RemindersEvent.EnabledChanged -> update { copy(enabled = event.enabled) }
            is RemindersContract.RemindersEvent.FrequencySelected -> update { copy(intervalMinutes = event.intervalMinutes) }
            is RemindersContract.RemindersEvent.GoalNotifyChanged -> update { copy(goalNotify = event.enabled) }
            is RemindersContract.RemindersEvent.StreakNotifyChanged -> update { copy(streakNotify = event.enabled) }
            is RemindersContract.RemindersEvent.AchievementNotifyChanged -> update { copy(achievementNotify = event.enabled) }
            is RemindersContract.RemindersEvent.SoundChanged -> update { copy(soundEnabled = event.enabled) }

            is RemindersContract.RemindersEvent.PickerOpened -> intent { reduce { state.copy(picker = event.target) } }

            RemindersContract.RemindersEvent.PickerDismissed -> intent { reduce { state.copy(picker = null) } }

            is RemindersContract.RemindersEvent.TimePicked -> intent {
                val wake = if (state.picker == RemindersContract.TimeTarget.WAKE) event.time else state.wakeTime
                val sleep = if (state.picker == RemindersContract.TimeTarget.SLEEP) event.time else state.sleepTime
                reduce { state.copy(wakeTime = wake, sleepTime = sleep, picker = null) }
                remindersUseCase.updateTimes(wake, sleep)
            }

            RemindersContract.RemindersEvent.EnableNotificationsClicked -> intent {
                postSideEffect(RemindersContract.SideEffect.OpenNotificationSettings)
            }
        }
    }

    private fun update(change: ReminderConfigUiData.() -> ReminderConfigUiData) = intent {
        val config = state.config.change()
        reduce { state.copy(config = config) }
        remindersUseCase.updateConfig(config)
    }
}
