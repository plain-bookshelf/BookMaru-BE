package plain.bookmaru.domain.verification.presentation

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.domain.verification.presentation.dto.request.SendEmailRequestDto
import plain.bookmaru.domain.verificationcode.port.`in`.SendVerificationUseCase
import plain.bookmaru.domain.verificationcode.port.`in`.command.SendVerificationCodeCommand

@RestController
@RequestMapping("/api/email")
class VerificationAdapter(
    private val sendVerificationUseCase: SendVerificationUseCase
) {

    @PostMapping("/send")
    suspend fun sendVerification(@RequestBody request : SendEmailRequestDto) {
        val command = SendVerificationCodeCommand(request.email)

        sendVerificationUseCase.sendVerificationCode(command)
    }
}