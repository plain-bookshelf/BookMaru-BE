package plain.bookmaru.domain.verification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.EmailSendPort
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.model.EmailVerification
import plain.bookmaru.domain.verification.port.`in`.SendVerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.command.SendVerificationCodeCommand
import plain.bookmaru.domain.verification.port.out.EmailVerificationCodePort
import plain.bookmaru.domain.verification.scope.MailCoroutineScope

private val log = KotlinLogging.logger {}

@Service
class SendVerificationCodeService(
    private val emailVerificationCodePort: EmailVerificationCodePort,
    private val mailPort: EmailSendPort,
    private val mailScope: MailCoroutineScope,
    private val concurrencyPort: ConcurrencyPort
) : SendVerificationCodeUseCase {
    override suspend fun execute(command: SendVerificationCodeCommand) {
        val email = Email.from(command.email)
        val emailVerification = EmailVerification.create(email, command.codeType)

        emailVerificationCodePort.save(emailVerification)

        mailScope.launch {
            try {
                log.info { "인증 메일 발송을 시작합니다. codeType=${command.codeType}" }
                concurrencyPort.executeNetworkWithRetry("send-verification-email") {
                    mailPort.send(email.email.toString(), emailVerification.codeData.code)
                }
                log.info { "인증 메일 발송 작업을 등록했습니다. codeType=${command.codeType}" }
            } catch (e: Exception) {
                log.error(e) { "인증 메일 발송에 실패했습니다. codeType=${command.codeType}" }
            }
        }
    }
}
