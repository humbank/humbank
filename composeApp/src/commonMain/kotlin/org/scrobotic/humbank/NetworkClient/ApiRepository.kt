package org.scrobotic.humbank.NetworkClient

import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.AllAccount

interface ApiRepository {
    suspend fun login(username: String, password: String): UserSession
    suspend fun getAllAccounts(): List<AllAccount>

    suspend fun getTodaysTransactions(): List<Transaction>

    suspend fun executeTransfer(
        issuerUsername: String,
        amount: Double,
        transactionId: String,
        description: String
    ): Boolean

    suspend fun getBalance(): Double

    suspend fun validateToken(): Boolean

    suspend fun updateAccounts(updatedAccountsOut: String?): List<AllAccount>


    suspend fun createUser(firstName: String, lastName: String, username: String, pin: String, role: String): Boolean
    suspend fun createBusiness(businessName: String, ownerUsername: String, pin: String, description: String): Boolean

}