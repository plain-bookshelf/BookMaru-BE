package plain.bookmaru.domain.member.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.member.port.`in`.MemberUseCase
import plain.bookmaru.domain.member.presentation.dto.request.SignupRequestDto

@RestController
@RequestMapping("/api/member")
class MemberAdapter(
    private val memberUseCase: MemberUseCase
) {

    @PostMapping("/signup-member")
    suspend fun signupMember(@RequestBody request: SignupRequestDto) : ResponseEntity<SuccessResponse> {
        val command = request.toCommand()
        memberUseCase.signupMember(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Content-Type", "application/json")
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "유저 회원가입이 성공적으로 완료됬습니다.", ""))
    }
}