package plain.bookmaru.domain.verification.persistent

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.port.out.EmailVerifiedPort
import plain.bookmaru.domain.verification.model.EmailVerified
import plain.bookmaru.global.config.DbProtection
import java.time.Duration
import java.time.Instant

@Component
class EmailVerifiedPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    private val dbProtection: DbProtection
): EmailVerifiedPort {
    override suspend fun save(emailVerified: EmailVerified) = dbProtection.withTransaction {
        redisTemplate.opsForValue().set(
            emailVerified.email.toString(),
            "True",
            Duration.between(Instant.now(), emailVerified.expiredAt)
        )
    }

    override suspend fun load(email: String) : EmailVerified? = dbProtection.withReadOnly {
        val value = redisTemplate.opsForValue().get(email) ?: return@withReadOnly null
        EmailVerified(Email(email), value, Instant.now())
    }
}