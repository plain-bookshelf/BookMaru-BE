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
import plain.bookmaru.domain.member.port.`in`.SignupUseCase
import plain.bookmaru.domain.member.presentation.dto.request.SignupMemberRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.SignupOfficialRequestDto

@RestController
@RequestMapping("/api/member")
class MemberAdapter(
    private val signupUseCase: SignupUseCase
) {

    @PostMapping("/signup-member")
    @LogExecution
    suspend fun signupMember(
        @RequestBody request: SignupMemberRequestDto,
        @RequestParam platformType: String
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)
        val result = signupUseCase.signupMember(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "유저 회원가입이 성공적으로 완료됐습니다.", result))
    }

    @PostMapping("/signup-official")
    @LogExecution
    suspend fun signupOfficial(
        @RequestBody request: SignupOfficialRequestDto,
        @RequestParam platformType: String
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)
        val result = signupUseCase.signupOfficial(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "관계자 회원가입이 성공적으로 완료됐습니다.", result))
    }
}