package xyz.nietongxue.common.base

fun <T, U> Result<T>.flatmap(f: (T) -> Result<U>): Result<U> {
    return when {
        this.isSuccess -> f(this.getOrNull()!!)
        this.isFailure -> return Result.failure(this.exceptionOrNull()!!)
        else -> throw IllegalStateException("Result is neither success nor failure")
    }
}

fun <T> tryOrDefault(default:T, block: () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        default
    }
}

