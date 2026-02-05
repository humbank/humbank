package org.scrobotic.humbank.NetworkClient

import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.AllAccount

interface ApiRepository {
    suspend fun login(username: String, password: String): UserSession
    suspend fun getAllAccounts(): List<AllAccount>

    suspend fun getTodaysTransactions(): List<Transaction>
}