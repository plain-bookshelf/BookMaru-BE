package plain.bookmaru.domain.auth.presentation

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
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
import plain.bookmaru.domain.auth.presentation.dto.response.TokenResponseDto
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.persistent.util.RefreshCookieUtil
import plain.bookmaru.global.security.jwt.JwtProperties

@RestController
@RequestMapping("/api/auth")
class AuthAdapter(
    private val loginUseCase: LoginUseCase,
    private val reissueUseCase: ReissueUseCase,
    private val logoutUseCase: LogoutUseCase,

    private val jwtProperties: JwtProperties
) {

    @PostMapping("/login")
    @LogExecution
    suspend fun login(
        @RequestBody request: LoginMemberRequestDto,
        @RequestParam platformType: String
    ): ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)

        val result = loginUseCase.execute(command)

        val now = System.currentTimeMillis()
        val cookie = RefreshCookieUtil.createRefreshCookie(result.refreshToken, now + jwtProperties.refreshExp.toMillis())

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(SuccessResponse(CustomHttpStatus.CREATED, "로그인에 성공하였습니다.", TokenResponseDto.toResponse(result)))
    }

    @PutMapping("/reissue")
    @LogExecution
    suspend fun reissue(
        @RequestHeader("X-Refresh-Token") token: String,
        @RequestParam platformType: String
    ): ResponseEntity<SuccessResponse> {

        val command = ReissueCommand(token, PlatformType.valueOf(platformType))

        val result = reissueUseCase.execute(command)

        val now = System.currentTimeMillis()
        val cookie = RefreshCookieUtil.createRefreshCookie(result.refreshToken, now + jwtProperties.refreshExp.toMillis())

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(SuccessResponse(CustomHttpStatus.CREATED, "토큰 재발급에 성공하엿습니다.", TokenResponseDto.toResponse(result)))
    }

    @PostMapping("/logout")
    @LogExecution
    suspend fun logout(
        @AuthenticationPrincipal user: UserDetails,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<SuccessResponse> {
        val command = LogoutCommand(token, user.username)

        logoutUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse(CustomHttpStatus.OK, "로그아웃에 성공하였습니다.", ""))
    }

}