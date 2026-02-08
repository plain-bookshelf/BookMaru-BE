package plain.bookmaru.domain.verification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.verification.vo.EmailVerification
import plain.bookmaru.domain.verification.scope.MailCoroutineScope
import plain.bookmaru.domain.verification.port.`in`.SendVerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.command.SendVerificationCodeCommand
import plain.bookmaru.common.port.EmailSendPort
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.port.out.EmailVerificationCodePort

private val logger = KotlinLogging.logger {}

@Service
class SendVerificationCodeService(
    private val repository : EmailVerificationCodePort,
    private val mailPort : EmailSendPort,
    private val mailScope : MailCoroutineScope
) : SendVerificationCodeUseCase {
    override suspend fun sendVerificationCode(command: SendVerificationCodeCommand) {
        val email = Email.from(command.email)
        val emailVerification = EmailVerification.create(email)
        repository.save(emailVerification)

        mailScope.launch {
            try {
                logger.info { "인증코드 요청 발생: ${emailVerification.email.email}" }
                mailPort.send(email.email.toString(), emailVerification.code)
            } catch (e: Exception) {
                logger.error(e) { "이메일 발송 중 예기치 못한 문제 발생: ${emailVerification.email.email}" }
            }
        }
    }
}