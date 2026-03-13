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
        val email = command.email
        val registerToken = command.registerToken

        val member = memberPort.findByUsername(email)
            ?: throw NotFoundMemberException("$email 이메일 정보를 가진 유저 정보가 없습니다.")

        val loadRegisterToken = findPasswordPort.load(email)

        if (loadRegisterToken == null || loadRegisterToken != registerToken) {
            throw NotMatchVerificationCodeException("비정상적인 접근 입니다.")
        }
        findPasswordPort.delete(email)

        passwordUpdateProfessor.updatePassword(member, newPassword)

        refreshTokenPort.deleteByUsername(email)
    }
}