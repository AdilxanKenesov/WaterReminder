package com.visionsystems.waterreminder.presenter.screens.reminders

import com.visionsystems.waterreminder.navigation.AppNavigator
import javax.inject.Inject

class RemindersDirection @Inject constructor(
    private val navigator: AppNavigator
) : RemindersContract.Direction
