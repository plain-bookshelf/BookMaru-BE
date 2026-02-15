package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.vo.ObjectTime
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.port.out.JwtPort
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.auth.result.TokenResult
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.member.exception.AlreadyExistsMemberException
import plain.bookmaru.domain.member.exception.AlreadyUsedEmailException
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.port.`in`.SignupUseCase
import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.verification.exception.NotFoundEmailException
import plain.bookmaru.domain.verification.port.out.EmailVerifiedPort
import java.time.LocalDateTime

val log = KotlinLogging.logger {}

@Service
class SignupMemberService(
    private val memberPort: MemberPort,
    private val affiliationPort: AffiliationPort,
    private val emailVerifiedPort: EmailVerifiedPort,
    private val securityPort: SecurityPort,
    private val jwtPort: JwtPort
) : SignupUseCase {

    override suspend fun signupMember(command: SignupMemberCommand) : TokenResult {
        val affiliationName = command.affiliationName
        val profile = command.profile
        val accountInfo = command.accountInfo
        val authority = command.authority
        val email = command.email

        if (memberPort.findByUsername(accountInfo.username) != null) {
            throw AlreadyExistsMemberException("Username already exists : ${accountInfo.username}")
        }

        if (!email?.email.isNullOrBlank()) {
            val emailProxy = emailVerifiedPort.load(email.email)
            if (emailProxy == null) {
                throw NotFoundEmailException("잘못된 이메일 입니다 : $email")
            }
            if (memberPort.findByEmail(email) != null) {
                throw AlreadyUsedEmailException("Already used email : ${email.email}")
            }
        }

        val affiliation = affiliationPort.findByAffiliationName(affiliationName)

        val newMember = Member.create(
            affiliation = affiliation,
            profile = profile,
            accountInfo = AccountInfo(
                username = accountInfo.username,
                password = securityPort.passwordEncode(accountInfo.password)
            ),
            authority = authority,
            email = email,
            objectTime = ObjectTime(LocalDateTime.now(), LocalDateTime.now())
        )

        log.info { "회원가입 성공 : ${accountInfo.username}" }

        val savedMember = memberPort.save(newMember)

        return jwtPort.responseToken(
            id = savedMember.id!!,
            username = savedMember.accountInfo.username,
            platformType = command.platformType,
            authority = savedMember.authority,
            affiliation = savedMember.affiliation
        )

    }
}