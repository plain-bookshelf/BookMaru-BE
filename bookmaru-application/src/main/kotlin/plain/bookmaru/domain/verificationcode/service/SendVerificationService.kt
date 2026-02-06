package plain.bookmaru.domain.verificationcode.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.verificationcode.model.EmailVerification
import plain.bookmaru.domain.verificationcode.scope.MailCoroutineScope
import plain.bookmaru.domain.verificationcode.port.`in`.SendVerificationUseCase
import plain.bookmaru.domain.verificationcode.port.`in`.command.SendVerificationCodeCommand
import plain.bookmaru.common.port.EmailSendPort
import plain.bookmaru.domain.verificationcode.port.out.EmailVerificationRepositoryPort

private val logger = KotlinLogging.logger {}

@Service
class SendVerificationService(
    private val repository : EmailVerificationRepositoryPort,
    private val mailPort : EmailSendPort,
    private val mailScope : MailCoroutineScope
) : SendVerificationUseCase {
    override suspend fun sendVerificationCode(command: SendVerificationCodeCommand) {
        val verification = EmailVerification.create(command.email)
        repository.save(verification)

        mailScope.launch {
            try {
                logger.info { "인증코드 요청 발생: ${verification.email}" }
                mailPort.send(verification.email, verification.code)
            } catch (e: Exception) {
                logger.error(e) { "이메일 발송 중 예기치 못한 문제 발생: ${verification.email}" }
            }
        }
    }
}