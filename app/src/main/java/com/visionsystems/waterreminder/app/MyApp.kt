package com.visionsystems.waterreminder.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.visionsystems.waterreminder.data.source.local.notification.NotificationChannels
import com.visionsystems.waterreminder.di.ApplicationScope
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.ReminderScheduler
import com.visionsystems.waterreminder.domain.repository.WidgetRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var scheduler: ReminderScheduler

    @Inject
    lateinit var currentUser: CurrentUserProvider

    @Inject
    lateinit var widgetRepository: WidgetRepository

    @Inject
    @ApplicationScope
    lateinit var scope: CoroutineScope

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.createAll(this)
        widgetRepository.refresh()
        scope.launch {
            scheduler.ensureScheduled()
            currentUser.currentUid.drop(1).collect {
                scheduler.rescheduleAll()
                widgetRepository.refresh()
            }
        }
    }
}
