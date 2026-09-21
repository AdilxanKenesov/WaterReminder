package com.visionsystems.waterreminder.navigation

import androidx.navigation3.runtime.NavKey

sealed interface NavCommand {
    data class Push(val route: NavKey) : NavCommand
    data class Replace(val route: NavKey) : NavCommand
    data class ReplaceAll(val route: NavKey) : NavCommand
    data object Back : NavCommand
    data class SelectTab(val tab: MainTab) : NavCommand
}
