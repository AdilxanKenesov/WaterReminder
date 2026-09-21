package com.visionsystems.waterreminder.presenter.screens.insights

import com.visionsystems.waterreminder.navigation.AppNavigator
import javax.inject.Inject

class InsightsDirection @Inject constructor(
    private val navigator: AppNavigator
) : InsightsContract.Direction
