package plain.bookmaru.domain.member.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.auth.service.BlackListProfessor
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.exception.NotMatchExistingPasswordException
import plain.bookmaru.domain.member.port.`in`.ChangePasswordUseCase
import plain.bookmaru.domain.member.port.`in`.command.ChangePasswordCommand
import plain.bookmaru.domain.member.port.out.MemberDevicePort
import plain.bookmaru.domain.member.port.out.MemberPort

@Service
class ChangePasswordService(
    private val passwordUpdateProfessor: PasswordUpdateProfessor,
    private val refreshTokenPort: RefreshTokenPort,
    private val memberDevicePort: MemberDevicePort,
    private val blackListProfessor: BlackListProfessor,
    private val memberPort: MemberPort,
    private val securityPort: SecurityPort
) : ChangePasswordUseCase {
    override suspend fun execute(command: ChangePasswordCommand) {
        val username = command.username
        val accessToken = command.accessToken
        val resolvedAccessToken = accessToken.substringAfter("Bearer ").trim()

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("유저 정보가 없습니다.")

        if (securityPort.getOAuthProvider(resolvedAccessToken) != OAuthProvider.DEFAULT) {
            throw NotMatchExistingPasswordException("일반 로그인 계정만 비밀번호를 변경할 수 있습니다.")
        }

        val storedPassword = member.accountInfo?.password
            ?: throw NotFoundMemberException("유저 정보가 없습니다.")

        if (!securityPort.isPasswordMatch(command.existingPassword, storedPassword)) {
            throw NotMatchExistingPasswordException("기존 비밀번호가 일치하지 않습니다.")
        }

        passwordUpdateProfessor.updatePassword(member, command.newPassword)

        blackListProfessor.execute(resolvedAccessToken)
        memberDevicePort.deleteAllByMemberId(member.id!!)
        refreshTokenPort.deleteByUsername(username)
    }
}
