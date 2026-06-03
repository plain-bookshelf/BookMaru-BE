package plain.bookmaru.global.security

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.out.SecurityPort
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.global.security.jwt.ClaimKey
import plain.bookmaru.global.security.jwt.JwtParser
import java.util.Date

@Component
class SecurityPersistencePort(
    private val passwordEncoder: PasswordEncoder,
    private val jwtParser: JwtParser
) : SecurityPort {
    override suspend fun isPasswordMatch(rawPassword: String, newPassword: String): Boolean = withContext(Dispatchers.Default){
        return@withContext passwordEncoder.matches(rawPassword, newPassword)
    }

    override suspend fun passwordEncode(rawPassword: String): String = withContext(Dispatchers.Default) {
        passwordEncoder.encode(rawPassword)
    }

    override fun getExpiration(token: String): Date = jwtParser.getClaims(token).expiration

    override fun getOAuthProvider(token: String): OAuthProvider {
        val oAuthProvider = jwtParser.getClaims(token)[ClaimKey.OAUTH_PROVIDER.name] as? String
        return oAuthProvider?.let { OAuthProvider.valueOf(it) } ?: OAuthProvider.DEFAULT
    }
}