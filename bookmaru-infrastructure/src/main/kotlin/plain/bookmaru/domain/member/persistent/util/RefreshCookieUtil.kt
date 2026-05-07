package plain.bookmaru.domain.member.persistent.util

import org.springframework.http.ResponseCookie

class RefreshCookieUtil {
    companion object {
        fun createRefreshCookie(refreshToken: String, maxAge: Long): ResponseCookie
        = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("None")
                .build()
    }
}