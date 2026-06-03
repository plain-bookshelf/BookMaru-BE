package plain.bookmaru.domain.verification.persistent

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.model.EmailVerified
import plain.bookmaru.domain.verification.port.out.EmailVerifiedPort
import java.time.Duration
import java.time.Instant

@Component
class EmailVerifiedPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate
) : EmailVerifiedPort {

    companion object {
        private const val EMAIL_VERIFIED_PREFIX = "verification:verified:"
    }

    override suspend fun save(emailVerified: EmailVerified) {
        redisTemplate.opsForValue().set(
            generateKey(emailVerified.email.email),
            "True",
            Duration.between(Instant.now(), emailVerified.expiredAt)
        )
    }

    override suspend fun load(email: String): EmailVerified? {
        val value = redisTemplate.opsForValue().get(generateKey(email)) ?: return null
        return EmailVerified(Email(email), value, Instant.now())
    }

    private fun generateKey(email: String): String {
        return "$EMAIL_VERIFIED_PREFIX$email"
    }
}
