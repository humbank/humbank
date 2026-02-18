package org.scrobotic.humbank.NetworkClient

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.scrobotic.humbank.screens.TransactionInputScreen

interface ApiService {
    suspend fun login(loginOut: LoginOut): NetworkResult<LoginIn>
    suspend fun getAllAccounts(): NetworkResult<List<AllAccountsIn>>

    suspend fun getTodaysTransactions(): NetworkResult<List<TransactionsTodayIn>>

    suspend fun executeTransfer(transferOut: TransferOut): NetworkResult<Unit>

    suspend fun executeTransfer(
        token: String,
        issuerUsername: String,
        amount: Double,
        transactionId: String,
        description: String
    ): NetworkResult<String>

    suspend fun getBalance(): NetworkResult<Double>

    suspend fun validateToken(): NetworkResult<Unit>
}

class ApiServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
): ApiService {
    //POST
    override suspend fun login(loginOut: LoginOut): NetworkResult<LoginIn> =
        httpClient.safeRequest {
            post("$baseUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(loginOut)
            }
        }


    override suspend fun getAllAccounts(): NetworkResult<List<AllAccountsIn>> =
        httpClient.safeRequest {
            get("$baseUrl/get_all_users"){
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun getTodaysTransactions(): NetworkResult<List<TransactionsTodayIn>> =
        httpClient.safeRequest {
            get("$baseUrl/get_todays_transactions")
        }


    override suspend fun executeTransfer(transferOut: TransferOut): NetworkResult<Unit> =
        httpClient.safeRequest {
            post("$baseUrl/execute_transfer"){
                contentType(ContentType.Application.Json)
                setBody(transferOut)
            }
        }

    override suspend fun executeTransfer(
        token: String,
        issuerUsername: String,
        amount: Double,
        transactionId: String,
        description: String
    ): NetworkResult<String> {
        return try {
            val response = httpClient.post("$baseUrl/execute_transfer") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(
                    TransferOut(
                        issuer_username = issuerUsername,
                        amount = amount,
                        transaction_id = transactionId,
                        description = description
                    )
                )
            }


            val responseText = response.bodyAsText()
            println("DEBUG: Transfer response: $responseText")

            NetworkResult.Success(responseText)
        } catch (e: Exception) {
            println("Transfer API error: ${e.message}")
            e.printStackTrace()
            NetworkResult.Failure(e.message ?: "Transfer failed")
        }
    }

    override suspend fun getBalance(): NetworkResult<Double> =
        httpClient.safeRequest {
            get("$baseUrl/get_user_balance")
        }

    override suspend fun validateToken(): NetworkResult<Unit> =
        httpClient.safeRequest {
                get("$baseUrl/check_token_validity")
            }
}