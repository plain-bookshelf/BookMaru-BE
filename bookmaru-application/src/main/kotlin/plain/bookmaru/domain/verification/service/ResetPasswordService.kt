package plain.bookmaru.domain.verification.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberDevicePort
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
    private val memberDevicePort: MemberDevicePort,
    private val memberPort: MemberPort,
    private val findPasswordPort: FindPasswordPort
) : ResetPasswordUseCase {
    override suspend fun execute(command: ResetPasswordCommand) {
        val email = command.email

        val member = memberPort.findByUsername(email)
            ?: throw NotFoundMemberException("유저 정보를 찾을 수 없습니다.")

        val loadRegisterToken = findPasswordPort.load(email)
        if (loadRegisterToken == null || loadRegisterToken != command.registerToken) {
            throw NotMatchVerificationCodeException("비밀번호 재설정 인증 정보가 일치하지 않습니다.")
        }

        findPasswordPort.delete(email)
        passwordUpdateProfessor.updatePassword(member, command.newPassword)

        memberDevicePort.deleteAllByMemberId(member.id!!)
        refreshTokenPort.deleteByUsername(email)
    }
}
