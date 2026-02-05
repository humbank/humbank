package org.scrobotic.humbank.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember


@Composable
fun rememberNavigator(start: Screen = Screen.Login): Navigator {
    return remember { Navigator(start) }
}


class Navigator(
    start: Screen = Screen.Login
) {
    private val backStack = mutableStateListOf(start)

    val current: Screen
        get() = backStack.last()

    fun push(screen: Screen) {
        backStack += screen
    }

    fun pop(): Boolean {
        if (backStack.size > 1) {
            backStack.removeLast()
            return true
        }
        return false
    }

    fun replace(screen: Screen) {
        backStack.clear()
        backStack += screen
    }

    fun resetToRoot() {
        val root = backStack.first()
        backStack.clear()
        backStack += root
    }
}