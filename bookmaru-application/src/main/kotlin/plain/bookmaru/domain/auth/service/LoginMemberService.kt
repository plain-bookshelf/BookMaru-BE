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
        log.info { "유저 로그인 시도." }

        val member = memberPort.findByEmail(command.accountInfo.username)
            ?: throw NotFoundMemberException("유저 정보를 찾지 못 했습니다.")

        return validationAndResponse(command, member, command.platformType)
    }

    private suspend fun validationAndResponse(
        command: LoginMemberCommand,
        member: Member,
        platformType: PlatformType
    ): TokenResult {
        log.info { "로그인을 할 수 있는 유저인지 검증을 시작합니다." }

        val encodedPassword = member.accountInfo?.password
            ?: throw NotFoundMemberException("유저 정보를 찾지 못 했습니다.")

        if (!securityPort.isPasswordMatch(command.accountInfo.password!!, encodedPassword)) {
            throw PasswordNotMatchException("아이디나 비밀번호 정보가 일치하지 않습니다.")
        }

        affiliationPort.findById(member.affiliationId!!)
            ?: throw NotFoundAffiliationException("소속 정보를 찾을 수 없습니다.")

        if (member.deleteStatus == true) {
            throw NotFoundMemberException("유저 정보를 찾지 못 했습니다.")
        }

        log.info { "로그인을 완료했습니다." }

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
            syncDeviceToken(member.id, platformType, command.deviceToken)
        }
    }

    private suspend fun syncDeviceToken(memberId: Long, platformType: PlatformType, deviceToken: String?) {
        if (platformType == PlatformType.WEB || deviceToken.isNullOrBlank()) return

        runCatching {
            memberDevicePort.upsert(memberId, deviceToken, platformType)
        }.onFailure {
            log.warn(it) { "deviceToken 등록 또는 갱신 중 오류가 발생했습니다. memberId=$memberId" }
        }
    }
}
