package org.scrobotic.humbank.screens

import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.UserSession

sealed interface Screen {
    data class Home(val userSession: UserSession) : Screen

    object UserProfile : Screen

    object Settings : Screen

    object Search : Screen

    data class Profile(val receiverAccount: AllAccount) : Screen

    data class TransactionInput(
        val senderAccount: AllAccount,
        val receiverAccount: AllAccount?
    ): Screen

    object Login: Screen
}