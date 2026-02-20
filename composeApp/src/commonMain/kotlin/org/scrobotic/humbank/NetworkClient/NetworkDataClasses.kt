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

@Serializable
data class TransferIn(
    val message: String? = null
)

@Serializable
data class UpdateAccountsOut(
    val time: String?
)

@Serializable
data class CreateUserOut(
    val first_name: String,
    val last_name: String,
    val username: String,
    val pin: String,
    val role: String
)

@Serializable
data class CreateBusinessOut(
    val business_name: String,
    val owner_username: String,
    val pin: String,
    val description: String
)

@Serializable
data class CreateUserResponse(val message: String, val id: Int, val username: String)
@Serializable
data class CreateBusinessResponse(val message: String, val id: Int)