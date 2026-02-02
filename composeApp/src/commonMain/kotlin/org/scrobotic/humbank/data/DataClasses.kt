package org.scrobotic.humbank.data


import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class UserSession(
    val token: String,
    val username: String
)


data class Transaction @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val sender: String,
    val receiver: String,
    val amount: Double,
    val pureDescription: String,
    val created: Instant,
    val currentBalance: Double
)


data class Account(
    val username: String,
    val fullName: String,
    val balance: Double,
    val role: String
)

@Serializable
data class allAccount @OptIn(ExperimentalTime::class) constructor(
    val username: String,
    val role: String,
    val updated_at: Instant,
    val full_name: String,
)


