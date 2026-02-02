package org.scrobotic.humbank.screens

import org.scrobotic.humbank.data.Account
import org.scrobotic.humbank.data.UserSession

sealed interface Screen {
    data class Home(val userSession: UserSession) : Screen

    object UserProfile : Screen

    object Settings : Screen

    object Search : Screen

    data class Profile(val receiverAccount: Account) : Screen

    data class TransactionInput(val senderAccount: Account, val receiverAccount: Account?): Screen

    object Login: Screen
}