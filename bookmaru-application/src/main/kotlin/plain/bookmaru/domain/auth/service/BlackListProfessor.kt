package plain.bookmaru.domain.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.out.BlackListPort
import plain.bookmaru.domain.auth.port.out.SecurityPort

private val log = KotlinLogging.logger {}

@Service
class BlackListProfessor(
    private val securityPort: SecurityPort,
    private val blackListPort: BlackListPort
) {
    suspend fun execute(accessToken: String) {
        val expiration = securityPort.getExpiration(accessToken)
        val now = System.currentTimeMillis()
        val remainingTime = expiration.time - now

        log.info { "remaining time: $remainingTime" }

        if (remainingTime > 0) {
            blackListPort.save(accessToken, remainingTime)
        }
    }
}