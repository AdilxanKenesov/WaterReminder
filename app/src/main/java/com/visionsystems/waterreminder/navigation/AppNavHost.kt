package com.visionsystems.waterreminder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.visionsystems.waterreminder.presenter.screens.aboutyou.AboutYouScreen
import com.visionsystems.waterreminder.presenter.screens.addwater.AddWaterScreen
import com.visionsystems.waterreminder.presenter.screens.editprofile.EditProfileScreen
import com.visionsystems.waterreminder.presenter.screens.goalreached.GoalReachedScreen
import com.visionsystems.waterreminder.presenter.screens.goalready.GoalReadyScreen
import com.visionsystems.waterreminder.presenter.screens.main.MainScreen
import com.visionsystems.waterreminder.presenter.screens.onboarding.OnboardingScreen
import com.visionsystems.waterreminder.presenter.screens.schedule.ScheduleScreen
import com.visionsystems.waterreminder.presenter.screens.setgoal.SetGoalScreen
import com.visionsystems.waterreminder.presenter.screens.signin.SignInScreen
import com.visionsystems.waterreminder.presenter.screens.signup.SignUpScreen
import com.visionsystems.waterreminder.presenter.screens.verifyemail.VerifyEmailScreen

@Composable
fun AppNavHost(handler: AppNavigationHandler, startRoute: NavKey) {
    val backStack = rememberNavBackStack(startRoute)
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    LaunchedEffect(handler) {
        handler.commands.collect { command ->
            when (command) {
                is NavCommand.Push -> backStack.add(command.route)
                is NavCommand.Replace -> backStack[backStack.lastIndex] = command.route
                is NavCommand.ReplaceAll -> {
                    if (command.route == MainRoute) selectedTab = MainTab.HOME
                    backStack.resetTo(command.route)
                }
                NavCommand.Back -> if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                is NavCommand.SelectTab -> {
                    selectedTab = command.tab
                    while (backStack.size > 1 && backStack.last() != MainRoute) backStack.removeAt(backStack.lastIndex)
                }
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        sceneStrategies = listOf(BottomSheetSceneStrategy(), SinglePaneSceneStrategy()),
        entryProvider = entryProvider {
            entry<OnboardingRoute> { OnboardingScreen() }
            entry<SignInRoute> { SignInScreen() }
            entry<SignUpRoute> { SignUpScreen() }
            entry<VerifyEmailRoute> { VerifyEmailScreen() }
            entry<AboutYouRoute> { AboutYouScreen() }
            entry<ScheduleRoute> { ScheduleScreen() }
            entry<GoalReadyRoute> { GoalReadyScreen() }
            entry<MainRoute> { MainScreen(selectedTab) }
            entry<SetGoalRoute> { SetGoalScreen() }
            entry<EditProfileRoute> { EditProfileScreen() }
            entry<AddWaterRoute>(metadata = BottomSheetSceneStrategy.bottomSheet()) { AddWaterScreen() }
            entry<GoalReachedRoute> { GoalReachedScreen() }
        }
    )
}

private fun NavBackStack<NavKey>.resetTo(route: NavKey) {
    add(route)
    while (size > 1) removeAt(0)
}
