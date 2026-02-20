package org.scrobotic.humbank.NetworkClient

import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.AllAccount
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class ApiRepositoryImpl(
    private val apiService: ApiService,
) : ApiRepository {

    override suspend fun login(
        username: String,
        password: String
    ): UserSession {

        val loginOut = LoginOut(
            username = username,
            pin = password
        )

        return when (val result = apiService.login(loginOut)) {
            is NetworkResult.Success ->
                UserSession(
                    token = result.data.token,
                    username = result.data.username
                )

            is NetworkResult.Failure ->
                throw Exception(result.errorMessage)
        }
    }


    @OptIn(ExperimentalTime::class)
    override suspend fun getAllAccounts(): List<AllAccount> {
        println("DEBUG: Calling getAllAccounts API...")
        return when (val result = apiService.getAllAccounts()) {
            is NetworkResult.Success -> {
                println("DEBUG: Success - got ${result.data.size} accounts")
                // Use .map to transform the API data into a list of your objects
                result.data.map { accountData ->
                    AllAccount(
                        username = accountData.username,
                        role = accountData.role,
                        updated_at = accountData.updated_at,
                        full_name = accountData.full_name
                    )
                }
            }

            is NetworkResult.Failure -> {
                println("DEBUG: Failure - error: ${result.errorMessage}")

                val errorMsg = result.errorMessage?.takeIf { it.isNotBlank() }
                    ?: "Failed to fetch accounts. Please check your network connection."
                throw Exception(errorMsg)
            }
        }
    }


    @OptIn(ExperimentalTime::class)
    override suspend fun getTodaysTransactions(): List<Transaction> {
        return when (val result = apiService.getTodaysTransactions()){
            is NetworkResult.Success -> {
                result.data.map { transferData ->
                    Transaction(
                        id = transferData.transaction_id,
                        sender = transferData.payer_username,
                        receiver = transferData.issuer_username,
                        amount = transferData.amount.toDouble(),
                        description = transferData.description,
                        transaction_date = Instant.parse(transferData.transaction_date)
                    )

                }
            }

            is NetworkResult.Failure ->{
                throw Exception(result.errorMessage)
            }

        }
    }

    override suspend fun executeTransfer(
        token: String,
        issuerUsername: String,
        amount: Double,
        transactionId: String,
        description: String
    ): Boolean {
        return when (val result = apiService.executeTransfer(
            token = token,
            issuerUsername = issuerUsername,
            amount = amount,
            transactionId = transactionId,
            description = description
        )) {
            is NetworkResult.Success -> {
                // ✅ result.data is now a String: "Transfer completed"
                println("DEBUG: Transfer successful - ${result.data}")
                true
            }
            is NetworkResult.Failure -> {
                println("DEBUG: Transfer failed - ${result.errorMessage}")
                throw Exception(result.errorMessage ?: "Transfer failed")
            }
        }
    }


    override suspend fun getBalance(): Double {
        return when (val result = apiService.getBalance()) {
            is NetworkResult.Success -> {
                result.data
            }

            is NetworkResult.Failure -> {
                println("DEBUG: getBalance failed - ${result.errorMessage}")
                throw Exception(result.errorMessage ?: "Server Communication failed")
            }
        }

    }

    override suspend fun validateToken(): Boolean {
        return when (val result = apiService.validateToken()) {
            is NetworkResult.Success -> {
                true
            }

            is NetworkResult.Failure -> {
                println("DEBUG: validateToken failed - ${result.errorMessage}")
                false
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateAccounts(updatedAccountsOut: String?): List<AllAccount> {
        return when (val result = apiService.updateAccounts(UpdateAccountsOut(updatedAccountsOut))){
            is NetworkResult.Success -> {
                result.data.map { accountData ->
                    AllAccount(
                        username = accountData.username,
                        role = accountData.role,
                        updated_at = accountData.updated_at,
                        full_name = accountData.full_name
                    )
                }
            }

            is NetworkResult.Failure -> {
                throw Exception(result.errorMessage)
            }

        }


    }
}
