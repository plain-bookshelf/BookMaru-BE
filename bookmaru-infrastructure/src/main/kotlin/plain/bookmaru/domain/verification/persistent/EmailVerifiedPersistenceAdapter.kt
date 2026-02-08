package plain.bookmaru.domain.verification.persistent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.port.out.EmailVerifiedPort
import plain.bookmaru.domain.verification.vo.EmailVerified
import java.time.Duration
import java.time.Instant

@Component
class EmailVerifiedPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate
): EmailVerifiedPort {
    override suspend fun save(emailVerified: EmailVerified) {
        withContext(Dispatchers.IO) {
            redisTemplate.opsForValue().set(
                emailVerified.email.toString(),
                "True",
                Duration.between(Instant.now(), emailVerified.expiredAt)
            )
        }
    }

    override suspend fun load(email: String) : EmailVerified? = withContext(Dispatchers.IO) {
        val value = redisTemplate.opsForValue().get(email) ?: return@withContext null
        EmailVerified(Email(email), value, Instant.now())
    }
}