package plain.bookmaru.domain.verification.presentation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.verification.presentation.dto.request.SendEmailRequestDto
import plain.bookmaru.domain.verification.port.`in`.SendVerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.VerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.command.SendVerificationCodeCommand
import plain.bookmaru.domain.verification.port.`in`.command.VerificationCodeCommand
import plain.bookmaru.domain.verification.presentation.dto.request.VerificationCodeRequestDto

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/email")
class VerificationAdapter(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verificationCodeUseCase: VerificationCodeUseCase
) {

    @PostMapping("/send")
    suspend fun sendVerification(@RequestBody request : SendEmailRequestDto) : ResponseEntity<SuccessResponse> {
        val command = SendVerificationCodeCommand(request.email)

        sendVerificationCodeUseCase.sendVerificationCode(command)

        logger.info { "\"${request.email}\" 로 메시지가 정상적으로 전송되었습니다." }

        return ResponseEntity.status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(SuccessResponse.success(CustomHttpStatus.OK, "메시지가 정상적으로 전송되었습니다.", ""))
    }

    @PostMapping("/verification")
    suspend fun verificationCode(@RequestBody request : VerificationCodeRequestDto) : ResponseEntity<SuccessResponse> {
        val command = VerificationCodeCommand(request.email, request.verificationCode)

        verificationCodeUseCase.verificationCode(command)

        logger.info { "\"${request.email}\" 이메일 인증을 성공했습니다." }

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Content-Type", "application/json")
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "이메일 인증이 성공적으로 완료되었습니다.", ""))
    }
}