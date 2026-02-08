package plain.bookmaru.domain.member.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.vo.ObjectTime
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.member.exception.AlreadyExistsMemberException
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.port.`in`.MemberUseCase
import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.exception.NotFoundEmailException
import plain.bookmaru.domain.verification.port.out.EmailVerifiedPort
import java.time.LocalDateTime

@Service
class SignupMemberService(
    private val memberPort: MemberPort,
    private val affiliationPort: AffiliationPort,
    private val emailVerifiedPort: EmailVerifiedPort
) : MemberUseCase {

    override suspend fun signupMember(command: SignupMemberCommand) {
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
        }

        val affiliation = affiliationPort.findByAffiliationName(affiliationName)

        val newMember = Member.create(
            affiliation = affiliation,
            profile = profile,
            accountInfo = accountInfo,
            authority = authority,
            email = email,
            objectTime = ObjectTime(LocalDateTime.now(), LocalDateTime.now())
        )

        memberPort.save(newMember)
    }
}