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
    override suspend fun save(emailVerified: EmailVerified) {
        redisTemplate.opsForValue().set(
            emailVerified.email.email.toString(),
            "True",
            Duration.between(Instant.now(), emailVerified.expiredAt)
        )
    }

    override suspend fun load(email: String): EmailVerified? {
        val value = redisTemplate.opsForValue().get(email) ?: return null
        return EmailVerified(Email(email), value, Instant.now())
    }
}
