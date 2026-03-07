package plain.bookmaru.domain.auth.presentation

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.presentation.dto.response.TokenResponseDto
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.persistent.util.RefreshCookieUtil
import plain.bookmaru.global.security.jwt.JwtProperties

@Component
class WebOrAppResponseUtil(
    private val jwtProperties: JwtProperties
) {
    fun toWebOrAppTokenResponse(platformType: String, result: TokenResult, message: String) : ResponseEntity<SuccessResponse> {
        val parsedPlatformType = runCatching { PlatformType.valueOf(platformType) }.getOrDefault(PlatformType.WEB)

        return when (parsedPlatformType) {
            PlatformType.WEB -> {
                val now = System.currentTimeMillis()
                val cookie = RefreshCookieUtil.createRefreshCookie(
                    result.refreshToken,
                    now + jwtProperties.refreshExp.toMillis()
                )

                ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(
                        SuccessResponse.success(
                            CustomHttpStatus.CREATED,
                            message,
                            TokenResponseDto.toWebResponse(result)
                        )
                    )
            }

            else -> {
                ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                        SuccessResponse.success(
                            CustomHttpStatus.CREATED,
                            message,
                            TokenResponseDto.toAppResponse(result)
                        )
                    )
            }
        }
    }
}