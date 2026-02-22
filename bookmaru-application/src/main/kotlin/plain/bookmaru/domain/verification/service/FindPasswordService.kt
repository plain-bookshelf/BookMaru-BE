package plain.bookmaru.domain.verification.service

import plain.bookmaru.common.annotation.ReadOnlyService
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.verification.exception.NotFoundEmailException
import plain.bookmaru.domain.verification.exception.NotMatchEmailMemberException
import plain.bookmaru.domain.verification.exception.NotMatchVerificationCodeException
import plain.bookmaru.domain.verification.port.`in`.FindPasswordUseCase
import plain.bookmaru.domain.verification.port.`in`.command.FindPasswordCommand
import plain.bookmaru.domain.verification.port.out.EmailVerificationCodePort
import plain.bookmaru.domain.verification.vo.VerificationCodeType

@ReadOnlyService
class FindPasswordService(
    private val emailVerificationCodePort: EmailVerificationCodePort,
    private val memberPort: MemberPort
) : FindPasswordUseCase {
    override suspend fun execute(command: FindPasswordCommand): Boolean {
        val email = command.email
        val verificationCode = command.verificationCode
        val username = command.username

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 가진 유저 정보를 찾을 수 없습니다.")

        if (member.email?.email == null || member.email.email != email)
            throw NotMatchEmailMemberException("$email 을 사용하지 않는 유저입니다.")

        val emailVerification = emailVerificationCodePort.load(email)
            ?: throw NotFoundEmailException("$email 이메일 정보로 인증 코드가 전송되지 않았습니다.")

        if (emailVerification.codeData.code != verificationCode
            || emailVerification.codeData.codeType != VerificationCodeType.FIND_PASSWORD)
            throw NotMatchVerificationCodeException("$verificationCode 인증코드가 틀렸거나 다른 타입의 인증코드를 입력하였습니다.")

        return true
    }
}