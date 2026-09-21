package com.visionsystems.waterreminder.data.repository_impl

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.visionsystems.waterreminder.di.ApplicationScope
import com.visionsystems.waterreminder.domain.repository.WidgetRepository
import com.visionsystems.waterreminder.widget.HydroWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:ApplicationScope private val scope: CoroutineScope
) : WidgetRepository {

    override fun refresh() {
        scope.launch { HydroWidget().updateAll(context) }
    }
}
