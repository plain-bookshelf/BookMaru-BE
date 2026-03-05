package plain.bookmaru.domain.verification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.verification.exception.NotFoundEmailException
import plain.bookmaru.domain.verification.exception.NotMatchVerificationCodeException
import plain.bookmaru.domain.verification.port.`in`.VerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.command.VerificationCodeCommand
import plain.bookmaru.domain.verification.port.out.EmailVerificationCodePort
import plain.bookmaru.domain.verification.port.out.EmailVerifiedPort
import plain.bookmaru.domain.verification.model.EmailVerified
import plain.bookmaru.domain.verification.vo.VerificationCodeType

private val log = KotlinLogging.logger {}

@Service
class VerificationCodeService(
    private val emailVerificationCodePort: EmailVerificationCodePort,
    private val emailVerifiedPort: EmailVerifiedPort
) : VerificationCodeUseCase {

    override suspend fun execute(command: VerificationCodeCommand) {
        val email = command.email

        val emailVerification = emailVerificationCodePort.load(email)

        if (emailVerification?.email == null)
            throw NotFoundEmailException("$email 를 찾지 못 했습니다.")

        if (emailVerification.codeData.code != command.verificationCode &&
            emailVerification.codeData.codeType == VerificationCodeType.VERIFICATION_EMAIL)
            throw NotMatchVerificationCodeException("${command.verificationCode} 인증코드가 틀렸거나 다른 타입의 인증코드를 입력하였습니다.")

        emailVerificationCodePort.delete(email)

        log.info { "$email 인증 완료" }

        emailVerifiedPort.save(EmailVerified.create(emailVerification.email))
    }
}