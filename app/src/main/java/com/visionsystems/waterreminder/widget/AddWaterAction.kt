package com.visionsystems.waterreminder.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import dagger.hilt.android.EntryPointAccessors

class AddWaterAction : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val amountMl = parameters[AmountKey] ?: return
        EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
            .widgetUseCase()
            .addDrink(amountMl)
    }

    companion object {
        val AmountKey = ActionParameters.Key<Int>("amount_ml")
    }
}
