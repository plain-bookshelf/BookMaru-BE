package plain.bookmaru.domain.member.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.`in`.LogoutUseCase
import plain.bookmaru.domain.auth.port.`in`.command.LogoutCommand
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.exception.NotMatchExistingPasswordException
import plain.bookmaru.domain.member.port.`in`.ChangePasswordUseCase
import plain.bookmaru.domain.member.port.`in`.command.ChangePasswordCommand
import plain.bookmaru.domain.member.port.out.MemberPort

@Service
class ChangePasswordService(
    private val passwordUpdateProfessor: PasswordUpdateProfessor,
    private val logoutUseCase: LogoutUseCase,
    private val memberPort: MemberPort,
    private val securityPort: SecurityPort
) : ChangePasswordUseCase {
    override suspend fun execute(command: ChangePasswordCommand) {
        val newPassword = command.newPassword
        val username = command.username
        val existingPassword = securityPort.passwordEncode(command.existingPassword)
        val accessToken = command.accessToken

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 가진 유저 정보가 없습니다.")

        if (securityPort.isPasswordMatch(existingPassword, member.accountInfo!!.password ?: "")
            && securityPort.getOAuthProvider(accessToken) != OAuthProvider.DEFAULT) throw NotMatchExistingPasswordException("")

        passwordUpdateProfessor.updatePassword(member, newPassword)

        logoutUseCase.execute(LogoutCommand(accessToken, username))
    }
}