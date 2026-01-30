package org.scrobotic.humbank.NetworkClient

import org.scrobotic.humbank.data.UserSession

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
}