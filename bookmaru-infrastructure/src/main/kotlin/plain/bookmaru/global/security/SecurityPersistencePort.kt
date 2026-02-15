package plain.bookmaru.global.security

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.out.SecurityPort

@Component
class SecurityPersistencePort(
    private val passwordEncoder: PasswordEncoder,
) : SecurityPort {
    override fun isPasswordMatch(rawPassword: String, newPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, newPassword)
    }

    override fun passwordEncode(rawPassword: String): String = passwordEncoder.encode(rawPassword)

}