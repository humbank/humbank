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
data class AllAccountsIn @OptIn(ExperimentalTime::class) constructor(
    val username: String,
    val role: String,
    val updated_at: Instant,
    val full_name: String,
)


@Serializable
data class TransactionsTodayIn(
    val amount: String,
    val description: String,
    val issuer_username: String,
    val payer_username: String,
    val transaction_date: String,
    val transaction_id: String
)

@Serializable
data class TransferOut(
    val transaction_id: String,
    val amount: Double,
    val description: String,
    val issuer_username: String
)
