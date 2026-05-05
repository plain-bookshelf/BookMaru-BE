package plain.bookmaru.domain.verification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.verification.exception.NotFoundEmailException
import plain.bookmaru.domain.verification.exception.NotMatchVerificationCodeException
import plain.bookmaru.domain.verification.port.`in`.FindPasswordUseCase
import plain.bookmaru.domain.verification.port.`in`.command.FindPasswordCommand
import plain.bookmaru.domain.verification.port.out.EmailVerificationCodePort
import plain.bookmaru.domain.verification.port.out.FindPasswordPort
import plain.bookmaru.domain.verification.port.out.result.RegisterTokenResult
import plain.bookmaru.domain.verification.vo.VerificationCodeType
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class FindPasswordService(
    private val emailVerificationCodePort: EmailVerificationCodePort,
    private val memberPort: MemberPort,
    private val findPasswordPort: FindPasswordPort
) : FindPasswordUseCase {
    override suspend fun execute(command: FindPasswordCommand): RegisterTokenResult {
        val email = command.email

        memberPort.findByEmail(email)
            ?: throw NotFoundMemberException("유저 정보를 찾을 수 없습니다.")

        val emailVerification = emailVerificationCodePort.load(email)
            ?: throw NotFoundEmailException("인증 코드 전송 정보를 찾을 수 없습니다.")

        log.info { "비밀번호 찾기 인증 코드 검증을 시작합니다. codeType=${emailVerification.codeData.codeType}" }

        if (emailVerification.codeData.code != command.verificationCode ||
            emailVerification.codeData.codeType != VerificationCodeType.FIND_PASSWORD
        ) {
            throw NotMatchVerificationCodeException("인증코드가 틀렸거나 다른 타입의 인증코드를 입력하였습니다.")
        }

        emailVerificationCodePort.delete(email)

        val uuid = UUID.randomUUID().toString()
        findPasswordPort.save(uuid, email)

        return RegisterTokenResult(uuid)
    }
}
