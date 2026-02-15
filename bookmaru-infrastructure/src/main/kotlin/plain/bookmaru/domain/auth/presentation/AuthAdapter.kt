package plain.bookmaru.domain.auth.presentation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.auth.port.`in`.LoginUseCase
import plain.bookmaru.domain.auth.port.`in`.ReissueUseCase
import plain.bookmaru.domain.auth.port.`in`.command.ReissueCommand
import plain.bookmaru.domain.auth.presentation.dto.request.LoginMemberRequestDto

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/auth")
class AuthAdapter(
    private val loginUseCase: LoginUseCase,
    private val reissueUseCase: ReissueUseCase
) {

    @PostMapping("/login-member")
    suspend fun login(
        @RequestBody request: LoginMemberRequestDto,
        @RequestParam platformType: String
    ): ResponseEntity<SuccessResponse> {

        val command = request.toCommand(platformType)

        val result = loginUseCase.loginMember(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Content-Type", "application/json")
            .body(SuccessResponse(CustomHttpStatus.CREATED, "로그인에 성공하였습니다.", result))
    }

    @PutMapping("/reissue")
    suspend fun reissue(
        @RequestHeader("X-Refresh-Token") token: String,
        @RequestParam platformType: String
    ): ResponseEntity<SuccessResponse> {
        log.info { "토큰 재발급 시도" }

        val command = ReissueCommand.toCommand(token, platformType)

        val result = reissueUseCase.reissue(command)

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Content-Type", "application/json")
            .body(SuccessResponse(CustomHttpStatus.CREATED, "토큰 재발급에 성공하엿습니다.", result))
    }
}