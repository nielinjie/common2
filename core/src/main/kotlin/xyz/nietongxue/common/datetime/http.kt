package xyz.nietongxue.common.datetime

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun parseHttpDate(dateString: String, zone: ZoneId = ZoneOffset.UTC): LocalDateTime? {
    // HTTP date format is typically in RFC 1123 format: "EEE, dd MMM yyyy HH:mm:ss zzz"
    val rfc1123Format = DateTimeFormatter.RFC_1123_DATE_TIME

    return try {
        // Parse the date string as ZonedDateTime first
        val zonedDateTime = ZonedDateTime.parse(dateString, rfc1123Format)
        // Convert to LocalDateTime in UTC or system default timezone
        zonedDateTime.withZoneSameInstant(zone).toLocalDateTime()
    } catch (e: Exception) {
        null // Return null if parsing fails
    }
}

fun formatToCustomStyle(dateString: String, outputPattern: String = "yyyy-MM-dd HH:mm:ss"): String? {
    val localDateTime = parseHttpDate(dateString)
    return localDateTime?.format(DateTimeFormatter.ofPattern(outputPattern))
}
