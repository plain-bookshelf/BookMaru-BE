package plain.bookmaru.global.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.`in`.CustomOAuth2UseCase
import plain.bookmaru.domain.auth.port.`in`.command.CustomOAuth2Command
import plain.bookmaru.domain.auth.port.out.result.LoginResult
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.global.security.jwt.JwtProperties

@Component
class OAuth2SuccessHandler(
    private val customOAuth2UseCase: CustomOAuth2UseCase,
    private val jwtProperties: JwtProperties
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal = authentication.principal as CustomOAuth2UserDetails
        val attr = principal.attributes

        val platformStr = request.cookies?.find { it.name == "platformType" }?.value ?: "WEB"
        val platformType = runCatching { PlatformType.valueOf(platformStr) }.getOrDefault(PlatformType.WEB)

        val command = CustomOAuth2Command(
            platformType = platformType,
            oAuthInfo = attr.oAuthInfo,
            email = attr.email,
            nickname = attr.nickname,
            profileImageUrl = attr.profileImageUrl
        )

        val result = runBlocking { customOAuth2UseCase.execute(command) }

        when (result) {
            is LoginResult.Success -> {
                val cookie = ResponseCookie.from("refreshToken", result.tokens.refreshToken)
                    .httpOnly(true).secure(false).path("/").maxAge(jwtProperties.refreshExp).build() // 배포 시 .secure(true)로 사용해야 됨
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

                response.sendRedirect("http://localhost:3000/oauth2/redirect?access_token=${result.tokens.accessToken}")
            }
            is LoginResult.NeedMoreInfo -> {
                response.sendRedirect("http://localhost:3000/signup/affiliation-name?register_token=${result.registerToken}")
            }
        }
    }
}