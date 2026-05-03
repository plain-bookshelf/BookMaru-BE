package plain.bookmaru.domain.verification.persistent

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.model.EmailVerification
import plain.bookmaru.domain.verification.port.out.EmailVerificationCodePort
import plain.bookmaru.domain.verification.vo.VerificationData
import java.time.Duration
import java.time.Instant

@Component
class EmailVerificationCodePersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) : EmailVerificationCodePort {
    override suspend fun save(emailVerification: EmailVerification) {
        val jsonData = objectMapper.writeValueAsString(emailVerification.codeData)

        redisTemplate.opsForValue().set(
            emailVerification.email.email.toString(),
            jsonData,
            Duration.between(Instant.now(), emailVerification.expiredAt)
        )
    }

    override suspend fun load(email: String): EmailVerification? {
        val jsonData = redisTemplate.opsForValue().get(email) ?: return null
        val codeData = objectMapper.readValue(jsonData, VerificationData::class.java)

        return EmailVerification(Email(email), codeData, Instant.now())
    }

    override suspend fun delete(email: String) {
        redisTemplate.unlink(email)
    }
}
