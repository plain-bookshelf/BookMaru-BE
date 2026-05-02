package plain.bookmaru.domain.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.`in`.LogoutUseCase
import plain.bookmaru.domain.auth.port.`in`.command.LogoutCommand
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.port.out.MemberDevicePort

private val log = KotlinLogging.logger {}

@Service
class LogoutService(
    private val refreshTokenPort: RefreshTokenPort,
    private val blackListProfessor: BlackListProfessor,
    private val memberDevicePort: MemberDevicePort
) : LogoutUseCase {
    override suspend fun execute(command: LogoutCommand) {
        val accessToken = resolveToken(command.accessToken)

        blackListProfessor.execute(accessToken)
        refreshTokenPort.deleteCurrentSession(command.username, command.platformType, command.deviceToken)

        if (command.platformType != PlatformType.WEB && !command.deviceToken.isNullOrBlank()) {
            runCatching {
                memberDevicePort.deleteByMemberUsername(command.username, command.deviceToken)
            }.onFailure {
                log.warn(it) { "logout 과정에서 deviceToken 제거 중 오류가 발생했습니다." }
            }
        }
    }

    private fun resolveToken(token: String): String = token.substringAfter("Bearer ").trim()
}
