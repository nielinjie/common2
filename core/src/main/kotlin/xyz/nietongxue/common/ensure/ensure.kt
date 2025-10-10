package xyz.nietongxue.common.ensure

interface EnsureResult<T : Any> {
    data class Find<T : Any>(val target: T) : EnsureResult<T>
    data class Created<T : Any>(val target: T) : EnsureResult<T>
    data object Failed : EnsureResult<Nothing>
}

fun <T : Any> EnsureResult<T>.get(): T {
    return when (this) {
        is EnsureResult.Created -> this.target
        is EnsureResult.Find -> this.target
        is EnsureResult.Failed -> throw Exception("Failed")
        else -> error("not supported ")
    }
}