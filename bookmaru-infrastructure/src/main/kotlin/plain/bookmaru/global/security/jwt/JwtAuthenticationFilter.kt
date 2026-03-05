package plain.bookmaru.global.security.jwt

import jakarta.servlet.DispatcherType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.http.HttpStatus
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtParser: JwtParser,
    private val redisTemplate: StringRedisTemplate
) : OncePerRequestFilter() {

    override fun shouldNotFilterAsyncDispatch(): Boolean {
        return false
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtParser.resolveToken(request)

        if (token != null) {
            try {
                if (jwtParser.validateToken(token)) {
                    val isLogout = redisTemplate.opsForValue().get(token)

                    if (isLogout != null && request.dispatcherType != DispatcherType.ASYNC) {
                        response.sendError(HttpStatus.SC_UNAUTHORIZED, "로그아웃 된 토큰입니다.")
                        log.info { "로그아웃된 토큰: $token" }
                        return
                    }

                    val authentication = jwtParser.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            } catch (e: Exception) {
                log.error { "JWT 인증 실패: ${e.message}" }

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다: ${e.message}")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}