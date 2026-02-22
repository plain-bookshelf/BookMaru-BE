package plain.bookmaru.domain.verification.persistent

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.model.EmailVerification
import plain.bookmaru.domain.verification.port.out. EmailVerificationCodePort
import plain.bookmaru.domain.verification.vo.VerificationData
import java.time.Duration
import java.time.Instant

@Component
class EmailVerificationCodePersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) : EmailVerificationCodePort {
    override suspend fun save(emailVerification : EmailVerification) = withContext(Dispatchers.IO) {
        val jsonData = objectMapper.writeValueAsString(emailVerification.codeData)

        redisTemplate.opsForValue().set(
            emailVerification.email.email.toString(),
            jsonData,
            Duration.between(Instant.now(), emailVerification.expiredAt)
        )
    }

    override suspend fun load(email: String) : EmailVerification? = withContext(Dispatchers.IO) {
        val jsonData = redisTemplate.opsForValue().get(email) ?: return@withContext null
        val codeData = objectMapper.readValue(jsonData, VerificationData::class.java)

        return@withContext EmailVerification(Email(email), codeData, Instant.now())
    }
}