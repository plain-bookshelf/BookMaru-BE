package plain.bookmaru.global.security.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.exception.ExpiredJwtTokenException
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.global.security.userdetails.CustomUserDetails
import java.nio.charset.StandardCharsets

val log = KotlinLogging.logger {}

@Component
class JwtParser(
    private val jwtProperties: JwtProperties
) {
    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)

        val userDetails = CustomUserDetails(
            id = claims[ClaimKey.MEMBER_ID.name].toString().toLong(),
            username = claims.subject,
            password = "",
            role = Authority.valueOf(claims[ClaimKey.AUTHORITY.name].toString()),
            affiliationId = claims[ClaimKey.AFFILIATION.name].toString().toLong(),
            isEnabled = true
        )
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val token = request.getHeader(jwtProperties.header)
        if (token != null && token.startsWith(jwtProperties.prefix)) {
            return token.substringAfter(jwtProperties.prefix).trim()
        }
        return null
    }

    fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            return true
        } catch (e: io.jsonwebtoken.security.SecurityException) {
            throw UnsupportedJwtException("지원하지 않는 토큰입니다. 사유: ${e.message}")
        } catch (e: MalformedJwtException) {
            throw UnsupportedJwtException("지원하지 않는 토큰입니다. 사유: ${e.message}")
        }
    }

    fun getClaims(token: String): Claims {
        val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            throw ExpiredJwtTokenException("만료된 토큰입니다.")
        } catch (e: Exception) {
            log.error { "회원정보를 받아오는 과정에서 예상치 못한 문제가 발생했습니다. ${e.message}" }
            throw e
        }
    }
}