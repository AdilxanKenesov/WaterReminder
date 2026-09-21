package com.visionsystems.waterreminder.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.visionsystems.waterreminder.MainActivity
import com.visionsystems.waterreminder.QuickAddActivity
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.module.WidgetUiData
import com.visionsystems.waterreminder.presenter.ui.theme.HydroOnPrimaryMuted
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimary
import com.visionsystems.waterreminder.presenter.ui.theme.HydroPrimaryDark
import com.visionsystems.waterreminder.presenter.ui.util.toVolume
import com.visionsystems.waterreminder.presenter.ui.util.toVolumeText
import dagger.hilt.android.EntryPointAccessors

class HydroWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(setOf(SMALL, WIDE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
            .widgetUseCase()
            .load()
        provideContent { WidgetContent(data) }
    }

    private companion object {
        val SMALL = DpSize(110.dp, 110.dp)
        val WIDE = DpSize(250.dp, 110.dp)
    }
}

private val White = ColorProvider(Color.White)
private val WhiteMuted = ColorProvider(HydroOnPrimaryMuted)
private val Teal = ColorProvider(HydroPrimary)
private val TealDark = ColorProvider(HydroPrimaryDark)

@Composable
private fun WidgetContent(data: WidgetUiData) {
    val openDialog = actionStartActivity<QuickAddActivity>()
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(24.dp)
            .background(Teal)
            .padding(14.dp)
            .clickable(if (data.isReady) openDialog else actionStartActivity<MainActivity>())
    ) {
        if (data.isReady) ReadyContent(data, openDialog) else SetupContent()
    }
}

@Composable
private fun ReadyContent(data: WidgetUiData, openDialog: Action) {
    val context = LocalContext.current
    val wide = LocalSize.current.width >= 250.dp
    val addCup = actionRunCallback<AddWaterAction>(actionParametersOf(AddWaterAction.AmountKey to data.cupMl))
    Column(modifier = GlanceModifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                provider = ImageProvider(R.drawable.ic_drop),
                contentDescription = null,
                colorFilter = ColorFilter.tint(White),
                modifier = GlanceModifier.size(16.dp)
            )
            Spacer(GlanceModifier.width(6.dp))
            Text(
                text = context.getString(R.string.today),
                style = TextStyle(color = WhiteMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            )
        }
        Spacer(GlanceModifier.defaultWeight())
        Text(
            text = data.consumedMl.toVolumeText(data.unit),
            style = TextStyle(color = White, fontSize = if (wide) 28.sp else 22.sp, fontWeight = FontWeight.Bold),
            maxLines = 1
        )
        Text(
            text = context.getString(R.string.of_goal, data.goalMl.toVolumeText(data.unit)),
            style = TextStyle(color = WhiteMuted, fontSize = 12.sp),
            maxLines = 1
        )
        Spacer(GlanceModifier.height(8.dp))
        LinearProgressIndicator(
            progress = data.progress,
            modifier = GlanceModifier.fillMaxWidth().height(6.dp),
            color = White,
            backgroundColor = ColorProvider(Color.White.copy(alpha = 0.25f))
        )
        Spacer(GlanceModifier.height(10.dp))
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            WidgetButton(
                text = "+${data.cupMl.toVolume(data.unit)}",
                onClick = addCup,
                filled = true,
                modifier = GlanceModifier.defaultWeight()
            )
            if (wide) {
                Spacer(GlanceModifier.width(8.dp))
                WidgetButton(
                    text = context.getString(R.string.widget_other),
                    onClick = openDialog,
                    filled = false,
                    modifier = GlanceModifier.defaultWeight()
                )
            }
        }
    }
}

@Composable
private fun SetupContent() {
    val context = LocalContext.current
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_drop),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = GlanceModifier.size(32.dp)
        )
        Spacer(GlanceModifier.height(8.dp))
        Text(
            text = context.getString(R.string.widget_open_app),
            style = TextStyle(color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun WidgetButton(text: String, onClick: Action, filled: Boolean, modifier: GlanceModifier = GlanceModifier) {
    Box(
        modifier = modifier
            .height(36.dp)
            .cornerRadius(12.dp)
            .background(if (filled) White else ColorProvider(Color.White.copy(alpha = 0.18f)))
            .clickable(onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(color = if (filled) TealDark else White, fontSize = 14.sp, fontWeight = FontWeight.Bold),
            maxLines = 1
        )
    }
}
