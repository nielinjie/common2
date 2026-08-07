package xyz.nietongxue.common.datetime

import java.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * https://en.wikipedia.org/wiki/ISO_8601#Durations
 * ISO-8601
 */

fun fromISO8601String(duration: String): Duration {
    return Duration.parse(duration)
}

fun fromString(duration: String): Duration {
    return fromISO8601String(duration)
}


fun Duration.toK(): kotlin.time.Duration {
    return this.toMillis().milliseconds
}