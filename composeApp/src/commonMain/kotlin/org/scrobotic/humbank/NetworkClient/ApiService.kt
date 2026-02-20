package org.scrobotic.humbank.NetworkClient

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ApiService {
    suspend fun login(loginOut: LoginOut): NetworkResult<LoginIn>
    suspend fun getAllAccounts(): NetworkResult<List<AllAccountsIn>>

    suspend fun getTodaysTransactions(): NetworkResult<List<TransactionsTodayIn>>

    suspend fun executeTransfer(transferOut: TransferOut): NetworkResult<Unit>

    suspend fun executeTransfer(
        issuerUsername: String,
        amount: Double,
        transactionId: String,
        description: String
    ): NetworkResult<String>

    suspend fun getBalance(): NetworkResult<Double>

    suspend fun validateToken(): NetworkResult<Unit>

    suspend fun updateAccounts(updatedAccountsOut: UpdateAccountsOut): NetworkResult<List<AllAccountsIn>>

    suspend fun createUser(createUserOut: CreateUserOut): NetworkResult<Unit>

    suspend fun createBusiness(createBusinessOut: CreateBusinessOut): NetworkResult<Unit>
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
        issuerUsername: String,
        amount: Double,
        transactionId: String,
        description: String
    ): NetworkResult<String> {
            val response = httpClient.post("$baseUrl/execute_transfer") {
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
        return if(responseText.contains("Error"))
            NetworkResult.Failure(responseText)
        else
            NetworkResult.Success(responseText)
    }

    override suspend fun getBalance(): NetworkResult<Double> =
        httpClient.safeRequest {
            get("$baseUrl/get_user_balance")
        }

    override suspend fun validateToken(): NetworkResult<Unit> =
        httpClient.safeRequest {
                get("$baseUrl/check_token_validity")
            }


    override suspend fun updateAccounts(updatedAccountsOut: UpdateAccountsOut): NetworkResult<List<AllAccountsIn>> =
        httpClient.safeRequest {
            post("$baseUrl/get_updated_accounts_after_time"){
                contentType(ContentType.Application.Json)
                setBody(updatedAccountsOut)
            }
        }

    override suspend fun createUser(user: CreateUserOut): NetworkResult<Unit> =
        httpClient.safeRequest {
            post("$baseUrl/create_user") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
        }

    override suspend fun createBusiness(business: CreateBusinessOut): NetworkResult<Unit> =
        httpClient.safeRequest {
            post("$baseUrl/create_business") {
                contentType(ContentType.Application.Json)
                setBody(business)
            }
        }
}