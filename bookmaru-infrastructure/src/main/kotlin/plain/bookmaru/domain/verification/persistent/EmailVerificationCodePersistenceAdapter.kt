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

    companion object {
        private const val EMAIL_VERIFICATION_CODE_PREFIX = "verification:code:"
    }

    override suspend fun save(emailVerification: EmailVerification) {
        val jsonData = objectMapper.writeValueAsString(emailVerification.codeData)

        redisTemplate.opsForValue().set(
            generateKey(emailVerification.email.email),
            jsonData,
            Duration.between(Instant.now(), emailVerification.expiredAt)
        )
    }

    override suspend fun load(email: String): EmailVerification? {
        val jsonData = redisTemplate.opsForValue().get(generateKey(email)) ?: return null
        val codeData = objectMapper.readValue(jsonData, VerificationData::class.java)

        return EmailVerification(Email(email), codeData, Instant.now())
    }

    override suspend fun delete(email: String) {
        redisTemplate.unlink(generateKey(email))
    }

    private fun generateKey(email: String): String {
        return "$EMAIL_VERIFICATION_CODE_PREFIX$email"
    }
}
