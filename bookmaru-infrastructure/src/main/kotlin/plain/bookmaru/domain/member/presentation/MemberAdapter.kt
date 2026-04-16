package plain.bookmaru.domain.member.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
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
import plain.bookmaru.domain.auth.port.`in`.SocialSignupUseCase
import plain.bookmaru.domain.auth.presentation.WebOrAppResponseUtil
import plain.bookmaru.domain.member.port.`in`.AffiliationInfoChangeUseCase
import plain.bookmaru.domain.member.port.`in`.ChangePasswordUseCase
import plain.bookmaru.domain.member.port.`in`.DeleteMemberUseCase
import plain.bookmaru.domain.member.port.`in`.NicknameChangeUseCase
import plain.bookmaru.domain.member.port.`in`.NicknameValidUseCase
import plain.bookmaru.domain.member.port.`in`.OftenReadBookTimeSetUseCase
import plain.bookmaru.domain.member.port.`in`.ProfileImageChangeUseCase
import plain.bookmaru.domain.member.port.`in`.SignupMemberUseCase
import plain.bookmaru.domain.member.port.`in`.SignupOfficialUseCase
import plain.bookmaru.domain.member.port.`in`.command.DeleteMemberCommand
import plain.bookmaru.domain.member.port.`in`.command.NicknameValidCommand
import plain.bookmaru.domain.member.presentation.dto.request.AffiliationInfoChangeRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.NicknameChangeRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.SocialSignupRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.PasswordChangeRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.OftenReadBookTimeRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.ProfileImageChangeRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.SignupMemberRequestDto
import plain.bookmaru.domain.member.presentation.dto.request.SignupOfficialRequestDto

@RestController
@RequestMapping("/api/member")
class MemberAdapter(
    private val signupMemberUseCase: SignupMemberUseCase,
    private val signupOfficialUseCase: SignupOfficialUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val oftenReadBookTimeSetUseCase: OftenReadBookTimeSetUseCase,
    private val socialSignupUseCase: SocialSignupUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
    private val affiliationInfoChangeUseCase: AffiliationInfoChangeUseCase,
    private val nicknameChangeUseCase: NicknameChangeUseCase,
    private val profileImageChangeUseCase: ProfileImageChangeUseCase,
    private val nicknameValidUseCase: NicknameValidUseCase,

    private val webOrAppResponseUtil: WebOrAppResponseUtil
) {

    @PostMapping("/signup-member")
    @LogExecution
    suspend fun signupMember(
        @RequestBody request: SignupMemberRequestDto,
        @RequestParam platformType: String
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)
        val result = signupMemberUseCase.execute(command)

        return webOrAppResponseUtil.toWebOrAppTokenResponse(platformType, result, CustomHttpStatus.CREATED,"유저 회원가입이 성공적으로 완료됐습니다.")
    }

    @PostMapping("/signup-official")
    @LogExecution
    suspend fun signupOfficial(
        @RequestBody request: SignupOfficialRequestDto,
        @RequestParam platformType: String
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)
        val result = signupOfficialUseCase.execute(command)

        return webOrAppResponseUtil.toWebOrAppTokenResponse(platformType, result, CustomHttpStatus.CREATED,"관계자 회원가입이 성공적으로 완료됐습니다.")
    }

    @PostMapping("/signup-social")
    @LogExecution
    suspend fun socialSignup(
        @RequestBody request : SocialSignupRequestDto,
        @RequestParam platformType: String
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)
        val result = socialSignupUseCase.execute(command)

        return webOrAppResponseUtil.toWebOrAppTokenResponse(platformType, result, CustomHttpStatus.CREATED, "소셜 회원가입이 성공적으로 완료됐습니다.")
    }

    @PatchMapping("/password-change")
    @LogExecution
    suspend fun passwordChange(
        @RequestBody request: PasswordChangeRequestDto,
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

    @DeleteMapping("/delete")
    @LogExecution
    suspend fun deleteMember(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestHeader("Authorization") accessToken: String
    ) : ResponseEntity<SuccessResponse> {

        val command = DeleteMemberCommand(principal.username, accessToken)

        deleteMemberUseCase.deleteMember(command)

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(SuccessResponse.success(CustomHttpStatus.NO_CONTENT, "유저 정보를 삭제하는데 성공했습니다.", ""))
    }

    @PatchMapping("/affiliation-change")
    @LogExecution
    suspend fun changeAffiliation(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestParam platformType: String,
        @RequestBody request: AffiliationInfoChangeRequestDto
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType, principal.username)

        val result = affiliationInfoChangeUseCase.execute(command)

        return webOrAppResponseUtil.toWebOrAppTokenResponse(platformType, result, CustomHttpStatus.OK, "소속 정보 변경이 완료되었습니다.")
    }

    @PatchMapping("nickname-change")
    @LogExecution
    suspend fun changeNickname(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestBody request: NicknameChangeRequestDto
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(principal.username)

        nicknameChangeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "유저 닉네임을 변경하는데 성공하였습니다.", ""))
    }

    @PatchMapping("/profileImage-change")
    @LogExecution
    suspend fun profileImageChange(
        @AuthenticationPrincipal principal: UserDetails,
        @RequestBody request: ProfileImageChangeRequestDto
    ) : ResponseEntity<SuccessResponse> {

        val command = request.toCommand(principal.username)

        profileImageChangeUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "프로필 정보를 수정하는데 성공하였습니다.", ""))
    }

    @GetMapping("/valid-nickname")
    @LogExecution
    suspend fun validNickname(
        @RequestParam nickname: String
    ): ResponseEntity<SuccessResponse> {
        val command = NicknameValidCommand(nickname)

        val result = nicknameValidUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "닉네임 검증에 성공했습니다.", result))
    }
}