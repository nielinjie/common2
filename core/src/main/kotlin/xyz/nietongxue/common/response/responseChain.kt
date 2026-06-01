package xyz.nietongxue.common.response


interface ResponseChain<RQ, RE> {
    val works: List<ResponseChainWorker<RQ, RE>>
    fun handle(request: RQ): ResponseChainResult<RE> {
        for (worker in works) {
            when (val re = worker.handle(request)) {
                is ResponseChainResult.Done -> return re
                is ResponseChainResult.Error -> return re
                is ResponseChainResult.NotMe -> continue
            }
        }
        return ResponseChainResult.Error(Exception("no worker handle this request"))
    }

    fun result(request: RQ): RE {
        return handle(request).let {
            when (it) {
                is ResponseChainResult.Done -> it.result
                is ResponseChainResult.Error -> throw it.error
                else -> error("no worker handle this request")
            }
        }
    }
    fun resultOrNull(request: RQ): RE? {
        return handle(request).let {
            when (it) {
                is ResponseChainResult.Done -> it.result
                is ResponseChainResult.Error -> null
                else -> null
            }
        }
    }
}

interface ResponseChainWorker<RQ, RE> {
    fun handle(request: RQ): ResponseChainResult<RE>
}

interface ResponseChainResult<out T> {
    data object NotMe : ResponseChainResult<Nothing>
    data class Done<T>(val result: T) : ResponseChainResult<T>

    //    data class Partial<T>(val result: T) : ResponseChainResult<T>
    data class Error<T>(val error: Throwable) : ResponseChainResult<T>
}

