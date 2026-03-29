package plain.bookmaru.common.management // 적절한 패키지 위치

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import org.springframework.dao.DataIntegrityViolationException

private val log = KotlinLogging.logger {}

class ConcurrencyManager {
    suspend fun <T> executeWithRetry(
        operationName: String,
        maxRetries: Int = 3,
        baseDelay: Long = 50L,
        block: suspend () -> T
    ): T {
        var attempt = 0

        while (attempt < maxRetries) {
            try {
                return block()

            } catch (e: DataIntegrityViolationException) {
                attempt++
                log.debug { "$operationName $attempt:$maxRetries 시도 중" }
                if (attempt >= maxRetries) {
                    log.error(e) { "[$operationName] 최대 재시도 횟수 초과로 최종 실패했습니다." }
                    throw IllegalStateException("현재 사용자가 많아 처리가 지연되고 있습니다. 잠시 후 다시 시도해주세요.")
                }
                delay(baseDelay * attempt)
            }
        }
        throw IllegalStateException("코드 에러 발생")
    }

    suspend fun <T> executeNetworkWithRetry(
        operationName: String,
        maxRetries: Int = 3,
        baseDelay: Long = 2000L,
        block: suspend () -> T
    ): T {
        var attempt = 0

        while (attempt < maxRetries) {
            try {
                return block()

            } catch (e: Exception) {
                attempt++
                log.debug { "$operationName $attempt:$maxRetries 시도 중" }
                if (attempt >= maxRetries) {
                    log.error(e) { "[$operationName] 최대 재시도 횟수 초과로 최종 실패했습니다." }
                    throw e
                }
                delay(baseDelay * attempt)
            }
        }
        throw Exception("코드 에러 발생")
    }
}