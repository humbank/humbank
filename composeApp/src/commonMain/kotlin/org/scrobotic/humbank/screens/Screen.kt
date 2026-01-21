package org.scrobotic.humbank.screens

sealed interface Screen {
    object Home : Screen

    data class UserProfile(
        val username: String
    ) : Screen

    object Settings : Screen
}