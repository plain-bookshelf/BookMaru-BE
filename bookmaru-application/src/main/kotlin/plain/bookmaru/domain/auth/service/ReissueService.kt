package plain.bookmaru.domain.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.exception.NotFoundAuthenticationException
import plain.bookmaru.domain.auth.port.`in`.ReissueUseCase
import plain.bookmaru.domain.auth.port.`in`.command.ReissueCommand
import plain.bookmaru.domain.auth.port.out.JwtPort
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberDevicePort
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class ReissueService(
    private val refreshTokenPort: RefreshTokenPort,
    private val jwtPort: JwtPort,
    private val memberPort: MemberPort,
    private val memberDevicePort: MemberDevicePort,
    private val affiliationPort: AffiliationPort,
    private val securityPort: SecurityPort
) : ReissueUseCase {

    override suspend fun execute(reissueCommand: ReissueCommand): TokenResult {
        val refreshToken = reissueCommand.refreshToken
        val platformType = reissueCommand.platformType

        val authentication = refreshTokenPort.findByTokenAndPlatformType(refreshToken, platformType)
            ?: throw NotFoundAuthenticationException("$refreshToken, $platformType 議댁옱?섏? ?딅뒗 ?좏겙 ?뺣낫?낅땲??")

        affiliationPort.findById(authentication.affiliationId)
            ?: throw NotFoundAffiliationException("?뚯냽 ?뺣낫瑜?李얠? 紐??덉뒿?덈떎.")

        val member = memberPort.findByUsername(authentication.username)
            ?: throw NotFoundMemberException("${authentication.username} ?좎? ?뺣낫瑜?李얠? 紐??덉뒿?덈떎.")

        if (member.deleteStatus == true) {
            throw NotFoundMemberException("${authentication.username} ?좎? ?뺣낫瑜?李얠? 紐??덉뒿?덈떎.")
        }

        log.info { "?좏겙 ?щ컻湲??꾨즺" }

        return jwtPort.responseToken(
            id = member.id!!,
            username = member.accountInfo!!.username,
            nickname = member.profile.nickname,
            platformType = platformType,
            authority = member.authority,
            affiliationId = member.affiliationId!!,
            oAuthProvider = securityPort.getOAuthProvider(refreshToken),
            profileImage = member.profile.profileImage.toString(),
            deviceToken = reissueCommand.deviceToken
        ).also {
            syncDeviceToken(member.id!!, platformType, reissueCommand.deviceToken)
        }
    }

    private suspend fun syncDeviceToken(memberId: Long, platformType: PlatformType, deviceToken: String?) {
        if (platformType == PlatformType.WEB || deviceToken.isNullOrBlank()) return

        runCatching {
            memberDevicePort.upsert(memberId, deviceToken, platformType)
        }.onFailure {
            log.warn(it) { "reissue ?뺤퐫?먯꽌 deviceToken ?깃린 ?갹텧 以묒뿉 ?ㅻ쪟媛 諛쒖깮?덉뒿?덈떎. memberId=$memberId" }
        }
    }
}
