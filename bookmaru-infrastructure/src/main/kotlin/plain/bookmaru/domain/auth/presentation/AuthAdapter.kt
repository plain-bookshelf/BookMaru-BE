package plain.bookmaru.domain.auth.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.auth.port.`in`.LoginUseCase
import plain.bookmaru.domain.auth.port.`in`.LogoutUseCase
import plain.bookmaru.domain.auth.port.`in`.ReissueUseCase
import plain.bookmaru.domain.auth.port.`in`.command.LogoutCommand
import plain.bookmaru.domain.auth.port.`in`.command.ReissueCommand
import plain.bookmaru.domain.auth.presentation.dto.request.LoginMemberRequestDto
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/api/auth")
class AuthAdapter(
    private val loginUseCase: LoginUseCase,
    private val reissueUseCase: ReissueUseCase,
    private val logoutUseCase: LogoutUseCase,

    private val webOrAppResponseUtil: WebOrAppResponseUtil
) {

    @PostMapping("/login")
    @LogExecution
    suspend fun login(
        @RequestBody request: LoginMemberRequestDto,
        @RequestParam platformType: String,
        @RequestHeader(name = "X-Device-Token", required = false) deviceToken: String?
    ): ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType, deviceToken)
        val result = loginUseCase.execute(command)

        return webOrAppResponseUtil.toWebOrAppTokenResponse(platformType, result, CustomHttpStatus.OK, "濡쒓렇?몄뿉 ?깃났?섏??듬땲??")
    }

    @PutMapping("/reissue")
    @LogExecution
    suspend fun reissue(
        @RequestHeader("X-Refresh-Token") token: String,
        @RequestParam platformType: String,
        @RequestHeader(name = "X-Device-Token", required = false) deviceToken: String?
    ): ResponseEntity<SuccessResponse> {

        val command = ReissueCommand(
            refreshToken = token,
            platformType = PlatformType.valueOf(platformType),
            deviceToken = deviceToken?.takeIf { it.isNotBlank() }
        )
        val result = reissueUseCase.execute(command)

        return webOrAppResponseUtil.toWebOrAppTokenResponse(platformType, result, CustomHttpStatus.OK, "?좏겙 ?щ컻湲됱뿉 ?깃났?섏뿿?듬땲??")
    }

    @PostMapping("/logout")
    @LogExecution
    suspend fun logout(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestHeader("Authorization") token: String,
        @RequestParam platformType: String,
        @RequestHeader(name = "X-Device-Token", required = false) deviceToken: String?
    ): ResponseEntity<SuccessResponse> {
        val command = LogoutCommand(
            accessToken = token,
            username = user.username.toString(),
            platformType = PlatformType.valueOf(platformType),
            deviceToken = deviceToken?.takeIf { it.isNotBlank() }
        )

        logoutUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(SuccessResponse(CustomHttpStatus.NO_CONTENT, "로그아웃에 성공하였습니다.", ""))
    }
}
