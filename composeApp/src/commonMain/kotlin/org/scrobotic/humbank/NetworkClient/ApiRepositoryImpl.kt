package org.scrobotic.humbank.NetworkClient

import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.allAccount
import kotlin.time.ExperimentalTime

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
    override suspend fun getAllAccounts(): List<allAccount> {
        return when (val result = apiService.getAllAccounts()) {
            is NetworkResult.Success -> {
                // Use .map to transform the API data into a list of your objects
                result.data.map { accountData ->
                    allAccount(
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