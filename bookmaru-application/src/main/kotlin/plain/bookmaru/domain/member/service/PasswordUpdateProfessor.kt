package plain.bookmaru.domain.member.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.member.exception.UsedPasswordException
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.port.out.MemberPort

@Service
class PasswordUpdateProfessor(
    private val memberPort: MemberPort,
    private val securityPort: SecurityPort
) {
    suspend fun updatePassword(member: Member, newPassword: String) {
        val newPassword = newPassword

        val newEncodePassword = securityPort.passwordEncode(newPassword)

        if (securityPort.isPasswordMatch(newPassword, member.accountInfo!!.password ?: ""))
            throw UsedPasswordException("이미 기존에 사용하던 비밀번호 값을 다시 사용했습니다.")

        member.modifyPassword(newEncodePassword)

        memberPort.save(member)
    }
}