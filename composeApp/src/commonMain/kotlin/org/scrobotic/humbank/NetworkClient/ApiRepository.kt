package org.scrobotic.humbank.NetworkClient

import org.scrobotic.humbank.data.UserSession

interface ApiRepository {
    suspend fun login(username: String, password: String): UserSession
}