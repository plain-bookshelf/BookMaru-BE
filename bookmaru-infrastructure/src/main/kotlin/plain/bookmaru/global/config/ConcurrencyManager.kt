package plain.bookmaru.global.config

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.stereotype.Component
import plain.bookmaru.common.port.ConcurrencyPort
import kotlin.coroutines.cancellation.CancellationException

private val log = KotlinLogging.logger {}

@Component
class ConcurrencyManager : ConcurrencyPort {
    override suspend fun <T> executeWithRetry(
        operationName: String,
        maxRetries: Int,
        baseDelay: Long,
        block: suspend () -> T
    ): T {
        var attempt = 0

        while (attempt < maxRetries) {
            try {
                return block()
            } catch (e: ConcurrencyFailureException) {
                attempt++
                log.debug { "$operationName 작업을 재시도합니다. ($attempt/$maxRetries)" }
                if (attempt >= maxRetries) {
                    log.error(e) { "$operationName 작업의 재시도 횟수를 초과했습니다." }
                    throw IllegalStateException("현재 요청이 많습니다. 잠시 후 다시 시도해주세요.")
                }
                delay(baseDelay * attempt)
            }
        }
        error("Unreachable: retry loop exited unexpectedly")
    }

    override suspend fun <T> executeNetworkWithRetry(
        operationName: String,
        maxRetries: Int,
        baseDelay: Long,
        shouldRetry: (Throwable) -> Boolean,
        block: suspend () -> T
    ): T {
        var attempt = 0

        while (attempt < maxRetries) {
            try {
                return block()
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                if (!shouldRetry(e)) {
                    log.warn(e) { "$operationName 작업은 재시도할 수 없는 오류여서 중단합니다. (${e.javaClass.simpleName})" }
                    throw e
                }

                attempt++
                log.debug { "네트워크 작업 $operationName 을 재시도합니다. ($attempt/$maxRetries)" }
                if (attempt >= maxRetries) {
                    log.error(e) { "네트워크 작업 $operationName 의 재시도 횟수를 초과했습니다." }
                    throw e
                }
                delay(baseDelay * attempt)
            }
        }
        error("Unreachable: retry loop exited unexpectedly")
    }
}
