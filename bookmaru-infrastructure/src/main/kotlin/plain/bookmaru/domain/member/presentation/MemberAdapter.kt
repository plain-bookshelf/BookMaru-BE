package plain.bookmaru.domain.member.presentation

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
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.port.`in`.SignupUseCase
import plain.bookmaru.domain.member.presentation.dto.request.SignupRequestDto

@RestController
@RequestMapping("/api/member")
class MemberAdapter(
    private val signupUseCase: SignupUseCase
) {

    @PostMapping("/signup-member")
    @LogExecution
    suspend fun signupMember(
        @RequestBody request: SignupRequestDto,
        @RequestParam platformType: String
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)
        val result = signupUseCase.signupMember(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Content-Type", "application/json")
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "유저 회원가입이 성공적으로 완료됬습니다.", result))
    }
}