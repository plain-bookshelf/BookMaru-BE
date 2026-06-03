package plain.bookmaru.common.port

import java.net.ConnectException
import java.net.SocketTimeoutException

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
                    e is SocketTimeoutException ||
                    e is ConnectException
        },
        block: suspend () -> T
    ): T
}
