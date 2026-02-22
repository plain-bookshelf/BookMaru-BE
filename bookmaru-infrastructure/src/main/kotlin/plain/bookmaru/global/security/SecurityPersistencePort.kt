package plain.bookmaru.global.security

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun passwordEncode(rawPassword: String): String = withContext(Dispatchers.Default) {
        passwordEncoder.encode(rawPassword)
    }

    override fun getExpiration(accessToken: String): Date = jwtParser.getClaims(accessToken).expiration
}