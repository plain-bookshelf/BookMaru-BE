package plain.bookmaru.domain.verification.persistent

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.verificationcode.model.EmailVerification
import plain.bookmaru.domain.verificationcode.port.out.EmailVerificationRepositoryPort
import java.time.Duration
import java.time.Instant

@Component
class VerificationPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate
) : EmailVerificationRepositoryPort {
    override suspend fun save(verification : EmailVerification) {
        redisTemplate.opsForValue().set(
            verification.email,
            verification.code,
            Duration.between(Instant.now(), verification.expiredAt)
        )
    }

    override suspend fun load(email: String) : EmailVerification? {
        val code = redisTemplate.opsForValue().get(email) ?: return null
        return EmailVerification(email, code, Instant.now())
    }
}