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
        val newPassword = command.newPassword
        val email = command.email
        val registerToken = command.registerToken

        val member = memberPort.findByUsername(email)
            ?: throw NotFoundMemberException("$email ?대찓???뺣낫瑜?媛吏??좎? ?뺣낫媛 ?놁뒿?덈떎.")

        val loadRegisterToken = findPasswordPort.load(email)

        if (loadRegisterToken == null || loadRegisterToken != registerToken) {
            throw NotMatchVerificationCodeException("鍮꾩젙?곸쟻???묎렐 ?낅땲??")
        }
        findPasswordPort.delete(email)

        passwordUpdateProfessor.updatePassword(member, newPassword)

        memberDevicePort.deleteAllByMemberId(member.id!!)
        refreshTokenPort.deleteByUsername(email)
    }
}
