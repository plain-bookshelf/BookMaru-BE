package plain.bookmaru.domain.verification.persistent

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.verification.model.EmailVerification
import plain.bookmaru.domain.verification.model.EmailVerified
import plain.bookmaru.domain.verification.vo.VerificationCodeType
import java.time.Duration

class VerificationRedisKeyPrefixTest {

    private val redisTemplate = mock(StringRedisTemplate::class.java)
    private val valueOperations = mock(ValueOperations::class.java) as ValueOperations<String, String>

    @Test
    fun `email verification code uses prefixed redis key`() = runBlocking {
        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)

        val adapter = EmailVerificationCodePersistenceAdapter(redisTemplate, jacksonObjectMapper())
        val verification = EmailVerification.create(
            email = Email("user@example.com"),
            codeType = VerificationCodeType.VERIFICATION_EMAIL
        )

        adapter.save(verification)
        adapter.delete("user@example.com")

        verify(valueOperations).set(
            eq("verification:code:user@example.com"),
            any(String::class.java),
            any(Duration::class.java)
        )
        verify(redisTemplate).unlink("verification:code:user@example.com")
        verify(valueOperations, never()).set(
            eq("user@example.com"),
            any(String::class.java),
            any(Duration::class.java)
        )
    }

    @Test
    fun `email verified state uses separate prefixed redis key`() = runBlocking {
        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        `when`(valueOperations.get("verification:verified:user@example.com")).thenReturn("True")

        val adapter = EmailVerifiedPersistenceAdapter(redisTemplate)
        val verified = EmailVerified.create(Email("user@example.com"))

        adapter.save(verified)
        val loaded = adapter.load("user@example.com")

        verify(valueOperations).set(
            eq("verification:verified:user@example.com"),
            eq("True"),
            any(Duration::class.java)
        )
        verify(valueOperations).get("verification:verified:user@example.com")
        assertNotNull(loaded)
        assertEquals("user@example.com", loaded!!.email.email)
        assertEquals("True", loaded.value)
    }
}
