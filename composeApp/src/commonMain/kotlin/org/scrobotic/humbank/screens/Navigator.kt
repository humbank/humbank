package org.scrobotic.humbank.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember


@Composable
fun rememberNavigator(start: Screen = Screen.Login): Navigator {
    return remember(start) { Navigator(start) }
}


class Navigator(
    start: Screen = Screen.Login
) {
    val canGoBack: Boolean
        get() = backStack.size > 1
    private val backStack = mutableStateListOf(start)
    var isGoingBack = false
        private set

    val current: Screen
        get() = backStack.last()

    fun push(screen: Screen) {
        isGoingBack = false
        backStack += screen
    }

    fun pop(): Boolean {
        if (backStack.size > 1) {
            isGoingBack = true
            backStack.removeLast()
            return true
        }
        return false
    }

    fun replace(screen: Screen) {
        isGoingBack = false
        backStack.clear()
        backStack += screen
    }
}