package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.model.Affiliation
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.port.out.JwtPort
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.exception.AlreadyExistsMemberException
import plain.bookmaru.domain.member.exception.AlreadyUsedEmailException
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.port.`in`.SignupMemberUseCase
import plain.bookmaru.domain.member.port.`in`.SignupOfficialUseCase
import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand
import plain.bookmaru.domain.member.port.`in`.command.SignupOfficialCommand
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.member.vo.LendingBook
import plain.bookmaru.domain.verification.exception.NotFoundEmailException
import plain.bookmaru.domain.verification.exception.NotMatchOfficialCodeException
import plain.bookmaru.domain.verification.model.OfficialCode
import plain.bookmaru.domain.verification.port.out.EmailVerifiedPort
import plain.bookmaru.domain.verification.port.out.OfficialCodePort

private val log = KotlinLogging.logger {}

@Service
class SignupMemberService(
    private val memberPort: MemberPort,
    private val affiliationPort: AffiliationPort,
    private val emailVerifiedPort: EmailVerifiedPort,
    private val securityPort: SecurityPort,
    private val jwtPort: JwtPort,
    private val officialCodePort: OfficialCodePort,
    private val transactionPort: TransactionPort
) : SignupMemberUseCase, SignupOfficialUseCase {

    override suspend fun execute(command: SignupMemberCommand): TokenResult {
        val affiliation = validationInfo(command.accountInfo.username, command.email, command.affiliationName)

        val newMember = Member.createMember(
            affiliationId = affiliation.id!!,
            profile = command.profile,
            accountInfo = AccountInfo(
                username = command.accountInfo.username,
                password = securityPort.passwordEncode(command.accountInfo.password ?: "")
            ),
            authority = Authority.ROLE_USER,
            email = command.email,
            lending = LendingBook()
        )

        log.info { "회원가입 검증을 완료했습니다." }

        return saveMemberAndReturnResponseToken(newMember, affiliation, command.platformType)
    }

    override suspend fun execute(command: SignupOfficialCommand): TokenResult {
        val affiliation = validationInfo(command.accountInfo.username, command.email, command.affiliationName)
        val officialCode = isMatch(command.affiliationName, command.verificationCode)

        val newMember = Member.createMember(
            affiliationId = affiliation.id!!,
            profile = command.profile,
            accountInfo = AccountInfo(
                username = command.accountInfo.username,
                password = securityPort.passwordEncode(command.accountInfo.password ?: "")
            ),
            authority = officialCode.role,
            email = command.email,
            lending = LendingBook()
        )

        log.info { "공식 회원가입 검증을 완료했습니다." }

        return saveMemberAndReturnResponseToken(newMember, affiliation, command.platformType)
    }

    /*
    private helper method
     */

    private suspend fun isMatch(affiliationName: String, code: String): OfficialCode {
        val affiliation = affiliationPort.findByAffiliationName(affiliationName)
            ?: throw NotFoundAffiliationException("소속 정보를 찾지 못 했습니다.")

        return officialCodePort.load(code, affiliation)
            ?: throw NotMatchOfficialCodeException("인증 코드가 일치하지 않습니다.")
    }

    private suspend fun validationInfo(username: String, email: Email, affiliationName: String): Affiliation {
        if (memberPort.findByUsername(username) != null) {
            throw AlreadyExistsMemberException("이미 존재하는 유저 아이디입니다.")
        }

        val emailProxy = emailVerifiedPort.load(email.email)
        if (emailProxy == null) {
            throw NotFoundEmailException("이메일 인증 정보를 찾지 못 했습니다.")
        }

        if (memberPort.findByEmail(email.email) != null) {
            throw AlreadyUsedEmailException("이미 사용되는 이메일입니다.")
        }

        return affiliationPort.findByAffiliationName(affiliationName)
            ?: throw NotFoundAffiliationException("존재하지 않는 도서관 정보입니다.")
    }

    private suspend fun saveMemberAndReturnResponseToken(
        newMember: Member,
        affiliation: Affiliation,
        platformType: PlatformType
    ): TokenResult {
        val savedMember = transactionPort.withTransaction {
            memberPort.save(newMember)
        }

        return jwtPort.responseToken(
            id = savedMember.id!!,
            username = savedMember.accountInfo!!.username,
            nickname = savedMember.profile.nickname,
            platformType = platformType,
            authority = savedMember.authority,
            affiliationId = affiliation.id!!,
            oAuthProvider = OAuthProvider.DEFAULT,
            profileImage = savedMember.profile.profileImage.toString()
        )
    }
}