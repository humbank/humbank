package org.scrobotic.humbank.data


import kotlin.time.ExperimentalTime
import kotlin.time.Instant


data class Transaction @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val sender: String,
    val receiver: String,
    val amount: Double,
    val pureDescription: String,
    val created: Instant,
    val currentBalance: Double,
    val isQrCode: Boolean = false
)

data class Account(
    val username: String,
    val fullName: String,
    val balance: Double,
    val role: String
)
