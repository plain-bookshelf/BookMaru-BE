package plain.bookmaru.domain.verification.persistent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.vo.EmailVerification
import plain.bookmaru.domain.verification.port.out. EmailVerificationCodePort
import java.time.Duration
import java.time.Instant

@Component
class EmailVerificationCodePersistenceAdapter(
    private val redisTemplate: StringRedisTemplate
) : EmailVerificationCodePort {
    override suspend fun save(emailVerification : EmailVerification) = withContext(Dispatchers.IO) {
        redisTemplate.opsForValue().set(
            emailVerification.email.email.toString(),
            emailVerification.code,
            Duration.between(Instant.now(), emailVerification.expiredAt)
        )
    }

    override suspend fun load(email: String) : EmailVerification? = withContext(Dispatchers.IO) {
        val code = redisTemplate.opsForValue().get(email) ?: return@withContext null
        EmailVerification(Email(email), code, Instant.now())
    }
}