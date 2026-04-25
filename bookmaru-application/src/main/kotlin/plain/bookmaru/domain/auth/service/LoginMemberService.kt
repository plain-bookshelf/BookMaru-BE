package plain.bookmaru.domain.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.exception.PasswordNotMatchException
import plain.bookmaru.domain.auth.port.`in`.LoginUseCase
import plain.bookmaru.domain.auth.port.`in`.command.LoginMemberCommand
import plain.bookmaru.domain.auth.port.out.JwtPort
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.port.out.MemberDevicePort
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class LoginMemberService(
    private val memberPort: MemberPort,
    private val memberDevicePort: MemberDevicePort,
    private val securityPort: SecurityPort,
    private val jwtPort: JwtPort,
    private val affiliationPort: AffiliationPort
) : LoginUseCase {

    override suspend fun execute(command: LoginMemberCommand): TokenResult {
        log.info { "${command.accountInfo.username} ??濡쒓렇?몄쓣 ?쒕룄 ?덉뒿?덈떎." }

        val member = memberPort.findByEmail(command.accountInfo.username)
            ?: throw NotFoundMemberException("${command.accountInfo.username} ?대찓?쇱쓣 媛吏??좎?媛 ?놁뒿?덈떎.")

        return validationAndResponse(command, member, command.platformType)
    }

    private suspend fun validationAndResponse(
        command: LoginMemberCommand,
        member: Member,
        platformType: PlatformType
    ): TokenResult {

        log.info { "${command.accountInfo.username} 瑜?李얜뒗???깃났?덉뒿?덈떎." }

        if (!securityPort.isPasswordMatch(command.accountInfo.password!!, member.accountInfo?.password
                ?: throw NotFoundMemberException("$member ??鍮꾨?踰덊샇 ?뺣낫瑜?李얠? 紐삵뻽?듬땲??"))
        ) {
            throw PasswordNotMatchException("${command.accountInfo.password} 鍮꾨?踰덊샇媛 ?쇱튂?섏? ?딆뒿?덈떎.")
        }

        affiliationPort.findById(member.affiliationId!!)
            ?: throw NotFoundAffiliationException("?뚯냽 ?뺣낫瑜?李얠? 紐??덉뒿?덈떎.")

        if (member.deleteStatus == true) {
            throw NotFoundMemberException("?좎? ?뺣낫瑜?李얠? 紐??덉뒿?덈떎.")
        }

        log.info { "濡쒓렇???깃났" }

        return jwtPort.responseToken(
            id = member.id!!,
            username = member.accountInfo!!.username,
            nickname = member.profile.nickname,
            platformType = platformType,
            authority = member.authority,
            affiliationId = member.affiliationId!!,
            oAuthProvider = OAuthProvider.DEFAULT,
            profileImage = member.profile.profileImage.toString(),
            deviceToken = command.deviceToken
        ).also {
            syncDeviceToken(member.id!!, platformType, command.deviceToken)
        }
    }

    private suspend fun syncDeviceToken(memberId: Long, platformType: PlatformType, deviceToken: String?) {
        if (platformType == PlatformType.WEB || deviceToken.isNullOrBlank()) return

        runCatching {
            memberDevicePort.upsert(memberId, deviceToken, platformType)
        }.onFailure {
            log.warn(it) { "deviceToken ?깃린 ?갹텧 以묒뿉 ?ㅻ쪟媛 諛쒖깮?덉뒿?덈떎. memberId=$memberId" }
        }
    }
}
