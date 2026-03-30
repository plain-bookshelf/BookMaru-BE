package plain.bookmaru.common.port

interface ConcurrencyPort {
    suspend fun <T> executeWithRetry(
        operationName: String,
        maxRetries: Int = 3,
        baseDelay: Long = 50L,
        block: suspend () -> T
    ): T

    suspend fun <T> executeNetworkWithRetry(
        operationName: String,
        maxRetries: Int = 3,
        baseDelay: Long = 2000L,
        shouldRetry: (Throwable) -> Boolean= { e ->
                    e is java.net.SocketTimeoutException ||
                    e is java.net.ConnectException
        },
        block: suspend () -> T
    ): T
}