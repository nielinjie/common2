package xyz.nietongxue.common.base

import kotlin.math.max

interface Matcher<T, R> {
    fun match(value: T): List<MatchResult<R>>
}


fun <T> MatchResult<T>.accept(scoreLine: Number = 0.5): T? {
    return if (score.value > scoreLine.toFloat()) value else null
}
/*
按不同的值进行合并。
 */

fun <T> List<MatchResult<T>>.compete(): List<MatchResult<T>> {
    val groups = this.groupBy { it.value }
    return groups.map { it.value.concur() }.filterNotNull().sortedByDescending { it.score }
}

/*
同样的 value，找到最大的 score
 */
fun <T> List<MatchResult<T>>.concur(): MatchResult<T>? {
    if (this.map { it.value }.distinct().size > 1) error("values are not the same")
    if (this.any { it.score.value < 0.0 }) return null
    return this.maxByOrNull { it.score }
}


fun <T> MatchResult<T>?.add(b: MatchResult<T>?): MatchResult<T>? {
    return this?.let { a ->
        b?.let { b ->
            require(a.value == b.value)
            if (a.score.value > 0 && b.score.value > 0) {
                MatchResult(a.value, Score(max(a.score.value, b.score.value)), a.reason + b.reason)
            } else null
        }
    }
}

data class MatchResult<R>(val value: R, val score: Score, val reason: String, val additional: Any? = null) {
    fun <R2> mapValue(f: (R) -> R2, newReason: String? = null): MatchResult<R2> {
        return MatchResult(f(value), score, newReason ?: reason, additional)
    }
}

data class Score(val value: Float) : Comparable<Score> {
    constructor(value: Double) : this(value.toFloat())
    constructor(value: Int) : this(value.toFloat())

    init {
        require(value in 0.0..1.0)
    }

    override fun compareTo(other: Score): Int {
        return value.compareTo(other.value)
    }
}