package plain.bookmaru.domain.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.`in`.LogoutUseCase
import plain.bookmaru.domain.auth.port.`in`.command.LogoutCommand
import plain.bookmaru.domain.auth.port.out.BlackListPort
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.port.out.SecurityPort

private val log = KotlinLogging.logger {}

@Service
class LogoutService(
    private val securityPort : SecurityPort,
    private val blackListPort: BlackListPort,
    private val refreshTokenPort: RefreshTokenPort
): LogoutUseCase {
    override suspend fun execute(command: LogoutCommand) {
        val username = command.username

        val accessToken = resolveToken(command.accessToken)

        val expiration = securityPort.getExpiration(accessToken)
        val now = System.currentTimeMillis()
        val remainingTime = expiration.time - now

        log.info { "remaining time: $remainingTime" }

        refreshTokenPort.deleteByUsername(username)

        if (remainingTime > 0) {
            blackListPort.save(accessToken, remainingTime)
        }
    }

    private fun resolveToken(token: String): String = token.substringAfter("Bearer ").trim()
}