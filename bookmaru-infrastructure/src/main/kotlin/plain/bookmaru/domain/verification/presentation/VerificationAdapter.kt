package plain.bookmaru.domain.verification.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.verification.port.`in`.FindIdUseCase
import plain.bookmaru.domain.verification.port.`in`.FindPasswordUseCase
import plain.bookmaru.domain.verification.port.`in`.OfficialCodeCreateUseCase
import plain.bookmaru.domain.verification.port.`in`.ResetPasswordUseCase
import plain.bookmaru.domain.verification.presentation.dto.request.SendEmailRequestDto
import plain.bookmaru.domain.verification.port.`in`.SendVerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.VerificationCodeUseCase
import plain.bookmaru.domain.verification.port.`in`.command.OfficialCodeCommand
import plain.bookmaru.domain.verification.presentation.dto.request.FindPasswordRequestDto
import plain.bookmaru.domain.verification.presentation.dto.request.ResetPasswordRequestDto
import plain.bookmaru.domain.verification.presentation.dto.request.VerificationCodeRequestDto
import plain.bookmaru.domain.verification.presentation.dto.response.UsernameResponseDto

@RestController
@RequestMapping("/api/verification")
class VerificationAdapter(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verificationCodeUseCase: VerificationCodeUseCase,
    private val officialCodeCreateUseCase: OfficialCodeCreateUseCase,
    private val findIdUseCase: FindIdUseCase,
    private val findPasswordUseCase: FindPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) {

    @PostMapping("/email/send")
    @LogExecution
    suspend fun sendVerification(
        @RequestBody request : SendEmailRequestDto,
        @RequestParam codeType: String
    ) : ResponseEntity<SuccessResponse> {
        val command = request.toCommand(codeType)

        sendVerificationCodeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "메시지가 정상적으로 전송되었습니다.", ""))
    }

    @PostMapping("/email/verify")
    @LogExecution
    suspend fun verificationCode(
        @RequestBody request : VerificationCodeRequestDto
    ) : ResponseEntity<SuccessResponse> {
        val command = request.toCommand()

        verificationCodeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "이메일 인증이 성공적으로 완료되었습니다.", ""))
    }

    @PostMapping("/officialCode/save")
    @LogExecution
    suspend fun saveOfficialCode(
        @RequestParam affiliationName: String
    ) : ResponseEntity<SuccessResponse> {
        val command = OfficialCodeCommand(affiliationName)

        officialCodeCreateUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "관계자 회원가입 인증 코드 생성이 성공적으로 완료되었습니다.", ""))
    }

    @PostMapping("/find-id")
    @LogExecution
    suspend fun findId(
        @RequestBody request: VerificationCodeRequestDto
    ): ResponseEntity<SuccessResponse> {
        val command = request.toCommand()

        val result = findIdUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "아이디 찾기에 성공했습니다.", UsernameResponseDto(result)))
    }

    @PostMapping("/find-password")
    @LogExecution
    suspend fun findPassword(
        @RequestBody request: FindPasswordRequestDto
    ) : ResponseEntity<SuccessResponse> {
        val command = request.toCommand()

        val result = findPasswordUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "비밀번호 정보를 수정할 수 있습니다.", result))
    }

    @PatchMapping("/password-reset")
    @LogExecution
    suspend fun passwordReset(
        @RequestBody request: ResetPasswordRequestDto
    ) : ResponseEntity<SuccessResponse> {
        val command = request.toCommand()

        resetPasswordUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "비밀번호 수정이 성공적으로 완료되었습니다. 이 계정에 대한 로그인이 전부 해지 되었습니다. 다시 로그인 하세요.", ""))
    }
}