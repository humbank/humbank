package org.scrobotic.humbank.misc

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


//@OptIn(ExperimentalTime::class)
//fun parseRfc1123ToInstant(input: String): Instant {
//    // Example: "Mon, 09 Feb 2026 15:24:50 GMT"
//    val parts = input.split(" ", ",").filter { it.isNotBlank() }
//
//    val day = parts[1].toInt()
//    val month = when (parts[2]) {
//        "Jan" -> 1; "Feb" -> 2; "Mar" -> 3; "Apr" -> 4
//        "May" -> 5; "Jun" -> 6; "Jul" -> 7; "Aug" -> 8
//        "Sep" -> 9; "Oct" -> 10; "Nov" -> 11; "Dec" -> 12
//        else -> error("Invalid month: ${parts[2]}")
//    }
//    val year = parts[3].toInt()
//
//    val time = parts[4].split(":")
//    val hour = time[0].toInt()
//    val minute = time[1].toInt()
//    val second = time[2].toInt()
//
//    return LocalDateTime(year, month, day, hour, minute, second)
//        .toInstant(TimeZone.UTC)
//}
