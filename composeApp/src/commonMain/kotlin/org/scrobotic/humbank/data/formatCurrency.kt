package org.scrobotic.humbank.data

import kotlin.math.roundToLong

fun Double.formatCurrency(): String {
    val rounded = (this * 100.0).roundToLong() / 100.0
    val parts = rounded.toString().split(".")
    val wholePart = parts[0].reversed().chunked(3).joinToString(".").reversed()
    val decimalPart = parts.getOrNull(1)?.padEnd(2, '0')?.take(2) ?: "00"
    return "$wholePart,$decimalPart"
}