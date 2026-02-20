package org.scrobotic.humbank.NetworkClient

import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.AllAccount

interface ApiRepository {
    suspend fun login(username: String, password: String): UserSession
    suspend fun getAllAccounts(): List<AllAccount>

    suspend fun getTodaysTransactions(): List<Transaction>

    suspend fun executeTransfer(
        token: String,
        issuerUsername: String,
        amount: Double,
        transactionId: String,
        description: String
    ): Boolean

    suspend fun getBalance(): Double

    suspend fun validateToken(): Boolean

    suspend fun updateAccounts(updatedAccountsOut: String?): List<AllAccount>
}