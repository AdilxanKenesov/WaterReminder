package com.visionsystems.waterreminder.navigation

import kotlinx.coroutines.flow.Flow

interface AppNavigationHandler {
    val commands: Flow<NavCommand>
}
