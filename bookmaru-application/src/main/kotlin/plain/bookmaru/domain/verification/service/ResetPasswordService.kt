package plain.bookmaru.domain.verification.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.service.PasswordUpdateProfessor
import plain.bookmaru.domain.verification.exception.NotMatchVerificationCodeException
import plain.bookmaru.domain.verification.port.`in`.ResetPasswordUseCase
import plain.bookmaru.domain.verification.port.`in`.command.ResetPasswordCommand
import plain.bookmaru.domain.verification.port.out.FindPasswordPort

@Service
class ResetPasswordService(
    private val passwordUpdateProfessor: PasswordUpdateProfessor,
    private val refreshTokenPort: RefreshTokenPort,
    private val memberPort: MemberPort,
    private val findPasswordPort: FindPasswordPort
) : ResetPasswordUseCase {
    override suspend fun execute(command: ResetPasswordCommand) {
        val newPassword = command.newPassword
        val username = command.username

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 가진 유저 정보가 없습니다.")

        if (findPasswordPort.load(username) == null) {
            throw NotMatchVerificationCodeException("비정상적인 접근 입니다.")
        }
        findPasswordPort.delete(username)

        passwordUpdateProfessor.updatePassword(member, newPassword)

        refreshTokenPort.deleteByUsername(username)
    }
}