package org.scrobotic.humbank.NetworkClient

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ApiService {
    suspend fun login(loginOut: LoginOut): NetworkResult<LoginIn>
    suspend fun getAllAccounts(): NetworkResult<List<AllAccountsIn>>

    suspend fun getTodaysTransactions(): NetworkResult<List<TransactionsTodayIn>>

    suspend fun executeTransfer(transferOut: TransferOut): NetworkResult<Unit>
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

}