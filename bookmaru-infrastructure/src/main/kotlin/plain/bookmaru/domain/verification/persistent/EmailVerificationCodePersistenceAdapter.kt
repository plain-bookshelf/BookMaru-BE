package plain.bookmaru.domain.verification.persistent

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
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
    private val objectMapper: ObjectMapper,
    @Qualifier("virtualDispatcher") private val virtualDispatcher: CoroutineDispatcher
) : EmailVerificationCodePort {
    override suspend fun save(emailVerification : EmailVerification) = withContext(virtualDispatcher) {
        val jsonData = objectMapper.writeValueAsString(emailVerification.codeData)

        redisTemplate.opsForValue().set(
            emailVerification.email.email.toString(),
            jsonData,
            Duration.between(Instant.now(), emailVerification.expiredAt)
        )
    }

    override suspend fun load(email: String) : EmailVerification? = withContext(virtualDispatcher) {
        val jsonData = redisTemplate.opsForValue().get(email) ?: return@withContext null
        val codeData = objectMapper.readValue(jsonData, VerificationData::class.java)

        return@withContext EmailVerification(Email(email), codeData, Instant.now())
    }

    override suspend fun delete(email: String) {
        withContext(virtualDispatcher) { redisTemplate.delete(email) }
    }
}