package com.visionsystems.waterreminder.widget

import com.visionsystems.waterreminder.domain.usecase.widget.WidgetUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun widgetUseCase(): WidgetUseCase
}
