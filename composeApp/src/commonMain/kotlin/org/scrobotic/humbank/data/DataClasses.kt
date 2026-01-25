package org.scrobotic.humbank.data

import kotlin.time.Instant

data class Transaction(
    val id: String,
    val sender: String,
    val receiver: String,
    val amount: Double,
    val created: Instant,
    val pureDescription: String,
    val currentBalance: Double
)