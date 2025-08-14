package xyz.nietongxue.common.base

import kotlinx.datetime.Clock

data class Record<out M>(
    val message: M,
    val level: LogLevel = LogLevel.INFO,
    val id: Id = v7(),
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)
