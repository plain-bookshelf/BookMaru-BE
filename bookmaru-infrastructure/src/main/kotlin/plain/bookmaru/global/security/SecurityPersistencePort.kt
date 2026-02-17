package plain.bookmaru.global.security

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.global.security.jwt.JwtParser
import java.util.Date

@Component
class SecurityPersistencePort(
    private val passwordEncoder: PasswordEncoder,
    private val jwtParser: JwtParser
) : SecurityPort {
    override fun isPasswordMatch(rawPassword: String, newPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, newPassword)
    }

    override fun passwordEncode(rawPassword: String): String = passwordEncoder.encode(rawPassword)

    override fun getExpiration(accessToken: String): Date = jwtParser.getClaims(accessToken).expiration

    override fun getUsername(accessToken: String): String = jwtParser.getAuthentication(accessToken).name
}