package plain.bookmaru.domain.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.exception.NotFoundAuthenticationException
import plain.bookmaru.domain.auth.port.`in`.ReissueUseCase
import plain.bookmaru.domain.auth.port.`in`.command.ReissueCommand
import plain.bookmaru.domain.auth.port.out.JwtPort
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.result.TokenResult
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class ReissueService(
    private val refreshTokenPort: RefreshTokenPort,
    private val jwtPort: JwtPort,
    private val memberPort: MemberPort
) : ReissueUseCase {

    override suspend fun reissue(reissueCommand: ReissueCommand): TokenResult {
        val refreshToken = reissueCommand.refreshToken
        val platformType = reissueCommand.platformType

        val authentication = refreshTokenPort.findByTokenAndPlatformType(refreshToken, platformType)
            ?: throw NotFoundAuthenticationException("$refreshToken, $platformType 존재하지 않는 토큰 정보입니다.")

        val member = memberPort.findByUsername(authentication.username)

        log.info { "토큰 재발급 완료" }

        return jwtPort.responseToken(
            id = member?.id!!,
            username = member.accountInfo.username,
            platformType = platformType,
            authority = member.authority,
            affiliation = member.affiliation
        )
    }
}