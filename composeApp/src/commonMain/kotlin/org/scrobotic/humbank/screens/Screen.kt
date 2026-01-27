package org.scrobotic.humbank.screens

sealed interface Screen {
    object Home : Screen

    object UserProfile : Screen

    object Settings : Screen
}