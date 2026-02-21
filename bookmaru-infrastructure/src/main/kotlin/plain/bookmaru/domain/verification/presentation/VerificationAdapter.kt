package plain.bookmaru.domain.verification.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.verification.port.`in`.OfficialCodeCreateUseCase
import plain.bookmaru.domain.verification.presentation.dto.request.SendEmailRequestDto
import plain.bookmaru.domain.verification.port.`in`.SendVerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.VerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.command.OfficialCodeCommand
import plain.bookmaru.domain.verification.port.`in`.command.SendVerificationCodeCommand
import plain.bookmaru.domain.verification.port.`in`.command.VerificationCodeCommand
import plain.bookmaru.domain.verification.presentation.dto.request.VerificationCodeRequestDto

@RestController
@RequestMapping("/api/verification")
class VerificationAdapter(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verificationCodeUseCase: VerificationCodeUseCase,
    private val officialCodeCreateUseCase: OfficialCodeCreateUseCase
) {

    @PostMapping("/email/send")
    @LogExecution
    suspend fun sendVerification(
        @RequestBody request : SendEmailRequestDto
    ) : ResponseEntity<SuccessResponse> {
        val command = SendVerificationCodeCommand(request.email)

        sendVerificationCodeUseCase.sendVerificationCode(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "메시지가 정상적으로 전송되었습니다.", ""))
    }

    @PostMapping("/email/verify")
    @LogExecution
    suspend fun verificationCode(
        @RequestBody request : VerificationCodeRequestDto
    ) : ResponseEntity<SuccessResponse> {
        val command = VerificationCodeCommand(request.email, request.verificationCode)

        verificationCodeUseCase.verificationCode(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "이메일 인증이 성공적으로 완료되었습니다.", ""))
    }

    @PostMapping("/officialCode/save")
    @LogExecution
    suspend fun saveOfficialCode(
        @RequestParam affiliationName: String
    ) : ResponseEntity<SuccessResponse> {
        val command = OfficialCodeCommand(affiliationName)

        officialCodeCreateUseCase.create(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "관계자 회원가입 인증 코드 생성이 성공적으로 완료되었습니다.", ""))
    }
}