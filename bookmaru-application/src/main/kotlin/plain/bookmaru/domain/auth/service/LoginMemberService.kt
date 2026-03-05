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
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.vo.Email

private val log = KotlinLogging.logger {}

@Service
class LoginMemberService(
    private val memberPort: MemberPort,
    private val securityPort: SecurityPort,
    private val jwtPort: JwtPort,
    private val affiliationPort: AffiliationPort
) : LoginUseCase{

    override suspend fun execute(command: LoginMemberCommand) : TokenResult {
        log.info { "${command.accountInfo.username} 이 로그인을 시도 했습니다." }

        if (command.accountInfo.username.contains("@")) {
            val member = memberPort.findByEmail(Email(command.accountInfo.username))
                ?: throw NotFoundMemberException("${command.accountInfo.username} 이메일을 가진 유저가 없습니다.")

            return validationAndResponse(command, member, command.platformType)
        } else {
            val member = memberPort.findByUsername(command.accountInfo.username)
                ?: throw NotFoundMemberException("${command.accountInfo.username} 아이디를 가진 유저가 없습니다.")

            return validationAndResponse(command, member, command.platformType)
        }
    }

    private suspend fun validationAndResponse(command: LoginMemberCommand, member: Member, platformType: PlatformType) :TokenResult {

        log.info { "${command.accountInfo.username} 를 찾는데 성공했습니다." }

        if (!securityPort.isPasswordMatch(command.accountInfo.password, member.accountInfo?.password ?:
        throw NotFoundMemberException("$member 의 비밀번호 정보를 찾지 못했습니다.")))
            throw PasswordNotMatchException("${command.accountInfo.password} 비밀번호가 일치하지 않습니다.")

        affiliationPort.findById(member.affiliationId!!)
            ?: throw NotFoundAffiliationException("소속 정보를 찾지 못 했습니다.")

        log.info { "로그인 성공" }

        return jwtPort.responseToken(
            id = member.id!!,
            username = member.accountInfo!!.username,
            platformType = platformType,
            authority = member.authority,
            affiliationId = member.affiliationId,
            oAuthProvider = OAuthProvider.DEFAULT,
            profileImage = member.profile.profileImage.toString()
        )
    }
}