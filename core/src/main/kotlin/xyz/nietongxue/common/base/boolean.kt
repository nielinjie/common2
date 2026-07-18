package xyz.nietongxue.common.base

fun <T> Boolean.mapOr(value: () -> T, other: () -> T): T {
    return if (this) value() else other()
}

fun <T> Boolean.mapOr(value: T, other: T): T {
    return if (this) value else other
}

fun <T> Boolean.mapOr(value: T, other: () -> T): T {
    return if (this) value else other()
}

fun <T> Boolean.mapOr(value: () -> T, other: T): T {
    return if (this) value() else other
}

fun <T> Boolean.mapOrNull(value: () -> T): T? {
    return if (this) value() else null
}

fun <T> Boolean.mapOrNull(value: T): T? {
    return if (this) value else null
}
