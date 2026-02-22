package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.member.exception.UsedPasswordException
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.port.out.MemberPort

private val logger = KotlinLogging.logger {}

@Service
class PasswordUpdateProfessor(
    private val memberPort: MemberPort,
    private val securityPort: SecurityPort,
    private val affiliationPort: AffiliationPort
) {
    suspend fun updatePassword(member: Member, newPassword: String) {
        val newPassword = newPassword

        val affiliation = affiliationPort.findById(member.affiliationId)
            ?: throw NotFoundAffiliationException("소속 정보를 찾지 못 했습니다.")

        val newEncodePassword = securityPort.passwordEncode(newPassword)

        if (securityPort.isPasswordMatch(newPassword, member.accountInfo.password))
            throw UsedPasswordException("이미 기존에 사용하던 비밀번호 값을 다시 사용했습니다.")

        member.retouchPassword(newEncodePassword)

        memberPort.save(member, affiliation)
    }
}