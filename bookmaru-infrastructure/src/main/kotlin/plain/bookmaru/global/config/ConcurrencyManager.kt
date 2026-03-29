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
                log.debug { "$operationName $attempt:$maxRetries 시도 중" }
                if (attempt >= maxRetries) {
                    log.error(e) { "[$operationName] 최대 재시도 횟수 초과로 최종 실패했습니다." }
                    throw IllegalStateException("현재 사용자가 많아 처리가 지연되고 있습니다. 잠시 후 다시 시도해주세요.")
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
        block: suspend () -> T
    ): T {
        var attempt = 0

        while (attempt < maxRetries) {
            try {
                return block()

            } catch (e: Exception) {
                if (e is CancellationException) throw e

                attempt++
                log.debug { "$operationName $attempt:$maxRetries 시도 중" }
                if (attempt >= maxRetries) {
                    log.error(e) { "[$operationName] 최대 재시도 횟수 초과로 최종 실패했습니다." }
                    throw e
                }
                delay(baseDelay * attempt)
            }
        }
        error("Unreachable: retry loop exited unexpectedly")
    }
}