package com.visionsystems.waterreminder.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.visionsystems.waterreminder.di.ApplicationScope
import com.visionsystems.waterreminder.domain.repository.NotificationRepository
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.usecase.drink.DrinkUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var drinkUseCase: DrinkUseCase

    @Inject
    lateinit var scheduler: ReminderScheduler

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    @ApplicationScope
    lateinit var scope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ADD_WATER -> {
                val amountMl = intent.getIntExtra(EXTRA_AMOUNT_ML, 0)
                notificationRepository.cancelReminder()
                val pending = goAsync()
                scope.launch {
                    try {
                        if (amountMl > 0) drinkUseCase.addDrink(amountMl) else drinkUseCase.addSelectedCup()
                    } finally {
                        pending.finish()
                    }
                }
            }

            ACTION_SNOOZE -> {
                notificationRepository.cancelReminder()
                scheduler.snooze(SNOOZE_MINUTES)
            }
        }
    }

    companion object {
        const val ACTION_ADD_WATER = "com.visionsystems.waterreminder.action.ADD_WATER"
        const val ACTION_SNOOZE = "com.visionsystems.waterreminder.action.SNOOZE"
        const val EXTRA_AMOUNT_ML = "extra_amount_ml"
        private const val SNOOZE_MINUTES = 15L
    }
}
