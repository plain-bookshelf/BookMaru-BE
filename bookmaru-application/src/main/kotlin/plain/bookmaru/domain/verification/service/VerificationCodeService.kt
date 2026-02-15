package plain.bookmaru.domain.verification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.verification.exception.NotFoundEmailException
import plain.bookmaru.domain.verification.exception.NotMatchVerificationCodeException
import plain.bookmaru.domain.verification.port.`in`.VerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.command.VerificationCodeCommand
import plain.bookmaru.domain.verification.port.out.EmailVerificationCodePort
import plain.bookmaru.domain.verification.port.out.EmailVerifiedPort
import plain.bookmaru.domain.verification.vo.EmailVerified

private val log = KotlinLogging.logger {}

@Service
class VerificationCodeService(
    private val emailVerificationCodePort: EmailVerificationCodePort,
    private val emailVerifiedPort: EmailVerifiedPort
) : VerificationCodeUseCase {

    override suspend fun verificationCode(command: VerificationCodeCommand) {
        val emailVerification = emailVerificationCodePort.load(command.email)

        if (emailVerification?.email == null) throw NotFoundEmailException(
            "${command.email} 를 찾지 못 했습니다.")
        if (emailVerification.code != command.verificationCode) throw NotMatchVerificationCodeException(
            "${command.verificationCode} 인증코드가 틀렸습니다.")

        log.info { "인증 완료" }

        emailVerifiedPort.save(EmailVerified.create(emailVerification.email))
    }
}