package xyz.nietongxue.common.response


interface ResponseChain<R, T> {
    val works: List<ResponseChainWorker<R, T>>
    fun handle(request: R): ResponseChainResult<T> {
        for (worker in works) {
            when (val re = worker.handle(request)) {
                is ResponseChainResult.Done -> return re
                is ResponseChainResult.Error -> return re
                is ResponseChainResult.NotMe -> continue
            }
        }
        return ResponseChainResult.Error(Exception("no worker handle this request"))
    }

    fun result(request: R): T {
        return handle(request).let {
            when (it) {
                is ResponseChainResult.Done -> it.result
                is ResponseChainResult.Error -> throw it.error
                else -> error("no worker handle this request")
            }
        }
    }
}

interface ResponseChainWorker<R, T> {
    fun handle(request: R): ResponseChainResult<T>
}

interface ResponseChainResult<T> {
    data object NotMe : ResponseChainResult<Nothing>
    data class Done<T>(val result: T) : ResponseChainResult<T>

    //    data class Partial<T>(val result: T) : ResponseChainResult<T>
    data class Error<T>(val error: Throwable) : ResponseChainResult<T>
}

