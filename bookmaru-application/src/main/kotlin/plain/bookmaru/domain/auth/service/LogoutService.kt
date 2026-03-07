package plain.bookmaru.domain.auth.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.`in`.LogoutUseCase
import plain.bookmaru.domain.auth.port.`in`.command.LogoutCommand
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort

@Service
class LogoutService(
    private val refreshTokenPort: RefreshTokenPort,
    private val blackListProfessor: BlackListProfessor
): LogoutUseCase {
    override suspend fun execute(command: LogoutCommand) {
        val username = command.username

        val accessToken = resolveToken(command.accessToken)

        blackListProfessor.execute(accessToken)

        refreshTokenPort.deleteByUsername(username)
    }

    private fun resolveToken(token: String): String = token.substringAfter("Bearer ").trim()
}