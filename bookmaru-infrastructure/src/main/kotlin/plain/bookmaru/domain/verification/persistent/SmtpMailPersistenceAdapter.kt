package plain.bookmaru.domain.verification.persistent

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import plain.bookmaru.common.port.EmailSendPort

@Component
class SmtpMailPersistenceAdapter(
    private val javaMailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine
) : EmailSendPort {

    override suspend fun send(email: String, code: String) {
        val message : MimeMessage = javaMailSender.createMimeMessage()

        val helper = MimeMessageHelper(message, true, "UTF-8")

        val context = Context().apply {
            setVariable("email", email)
            setVariable("code", code)
        }

        val htmlContent = templateEngine.process("EmailVerification", context)

        helper.setTo(email)
        helper.setSubject("[책마루] 인증 번호 안내")
        helper.setText(htmlContent, true)

        javaMailSender.send(message)
    }
}