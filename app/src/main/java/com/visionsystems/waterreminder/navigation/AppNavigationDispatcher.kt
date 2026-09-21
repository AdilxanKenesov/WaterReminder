package com.visionsystems.waterreminder.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNavigationDispatcher @Inject constructor() : AppNavigator, AppNavigationHandler {

    private val channel = Channel<NavCommand>(Channel.BUFFERED)

    override val commands: Flow<NavCommand> = channel.receiveAsFlow()

    private fun navigate(command: NavCommand) {
        channel.trySend(command)
    }

    override fun navigateTo(route: NavKey) = navigate(NavCommand.Push(route))

    override fun replaceTo(route: NavKey) = navigate(NavCommand.Replace(route))

    override fun replaceAll(route: NavKey) = navigate(NavCommand.ReplaceAll(route))

    override fun back() = navigate(NavCommand.Back)

    override fun selectTab(tab: MainTab) = navigate(NavCommand.SelectTab(tab))
}
