package plain.bookmaru.domain.member.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.member.port.`in`.ChangePasswordUseCase
import plain.bookmaru.domain.member.port.`in`.OftenReadBookTimeSetUseCase
import plain.bookmaru.domain.member.port.`in`.SignupMemberUseCase
import plain.bookmaru.domain.member.port.`in`.SignupOfficialUseCase
import plain.bookmaru.domain.member.presentation.dto.request.ChangePasswordRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.OftenReadBookTimeRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.SignupMemberRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.SignupOfficialRequestDto

@RestController
@RequestMapping("/api/member")
class MemberAdapter(
    private val signupMemberUseCase: SignupMemberUseCase,
    private val signupOfficialUseCase: SignupOfficialUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val oftenReadBookTimeSetUseCase: OftenReadBookTimeSetUseCase
) {

    @PostMapping("/signup-member")
    @LogExecution
    suspend fun signupMember(
        @RequestBody request: SignupMemberRequestDto,
        @RequestParam platformType: String
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)
        val result = signupMemberUseCase.execute(command)

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
        val result = signupOfficialUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse.success(CustomHttpStatus.CREATED, "관계자 회원가입이 성공적으로 완료됐습니다.", result))
    }

    @PatchMapping("/password-change")
    @LogExecution
    suspend fun passwordChange(
        @RequestBody request: ChangePasswordRequestDto,
        @RequestHeader("Authorization") accessToken: String,
        @AuthenticationPrincipal user: UserDetails,
    ) : ResponseEntity<SuccessResponse> {
        val command = request.toCommand(accessToken, user.username)

        changePasswordUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "비밀번호 변경이 성공적으로 완료 되었습니다. 재로그인 해주세요.", ""))
    }

    @PatchMapping("/often-read-book-time")
    @LogExecution
    suspend fun oftenReadBookTime(
        @RequestBody request: OftenReadBookTimeRequestDto,
        @AuthenticationPrincipal user: UserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = request.toCommand(user.username)

        oftenReadBookTimeSetUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "자주 책 보는 시간을 등록했습니다.", ""))
    }
}