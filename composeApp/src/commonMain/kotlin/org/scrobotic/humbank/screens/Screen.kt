package org.scrobotic.humbank.screens

import org.scrobotic.humbank.data.Account
import org.scrobotic.humbank.data.UserSession

sealed interface Screen {
    object Home : Screen

    object UserProfile : Screen

    object Settings : Screen

    object Search : Screen

    data class Profile(val username: String) : Screen

    data class TransactionInput(val account: Account): Screen

    object Login: Screen
}