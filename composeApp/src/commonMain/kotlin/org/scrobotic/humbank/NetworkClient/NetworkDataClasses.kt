package org.scrobotic.humbank.NetworkClient

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class LoginOut(
    val username: String,
    val pin: String
)

@Serializable
data class LoginIn(
    val token: String,
    val username: String
)

@Serializable
data class allAccountsIn @OptIn(ExperimentalTime::class) constructor(
    val username: String,
    val role: String,
    val updated_at: Instant,
    val full_name: String,
)
