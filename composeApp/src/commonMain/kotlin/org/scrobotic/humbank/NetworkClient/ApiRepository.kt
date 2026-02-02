package org.scrobotic.humbank.NetworkClient

import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.allAccount

interface ApiRepository {
    suspend fun login(username: String, password: String): UserSession
    suspend fun getAllAccounts(): List<allAccount>
}