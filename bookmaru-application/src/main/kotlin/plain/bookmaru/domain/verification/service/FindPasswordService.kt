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
import plain.bookmaru.domain.verification.vo.VerificationCodeType
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class FindPasswordService(
    private val emailVerificationCodePort: EmailVerificationCodePort,
    private val memberPort: MemberPort,
    private val findPasswordPort: FindPasswordPort
) : FindPasswordUseCase {
    override suspend fun execute(command: FindPasswordCommand): Boolean {
        val email = command.email
        val verificationCode = command.verificationCode

        memberPort.findByEmail(email)
            ?: throw NotFoundMemberException("$email 를 사용하는 유저 정보를 찾을 수 없습니다.")

        val emailVerification = emailVerificationCodePort.load(email)
            ?: throw NotFoundEmailException("$email 이메일 정보로 인증 코드가 전송되지 않았습니다.")

        log.info { "$verificationCode 비교 시작" }
        log.info { "${emailVerification.codeData.codeType} 타입 확인" }

        if (emailVerification.codeData.code != verificationCode
            || emailVerification.codeData.codeType != VerificationCodeType.FIND_PASSWORD)
            throw NotMatchVerificationCodeException("$verificationCode 인증코드가 틀렸거나 다른 타입의 인증코드를 입력하였습니다.")

        emailVerificationCodePort.delete(email)

        val uuid = UUID.randomUUID().toString()
        findPasswordPort.save(uuid, email)

        return true
    }
}