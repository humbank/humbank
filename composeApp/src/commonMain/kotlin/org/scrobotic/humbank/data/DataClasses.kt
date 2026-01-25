package org.scrobotic.humbank.data


import kotlin.time.Instant


data class Transaction(
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
    val account_id: String,
    val full_name: String,
    val pin: String,
    val balance: Double
)

