package plain.bookmaru.domain.verification.persistent

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.verification.port.out.FindPasswordPort
import java.time.Duration

@Component
class FindPasswordPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate
) : FindPasswordPort {
    override suspend fun save(registerToken: String, email: String) {
        redisTemplate.opsForValue().set(
            email,
            registerToken,
            Duration.ofMinutes(10)
        )
    }

    override suspend fun load(email: String): String? = redisTemplate.opsForValue().get(email)

    override suspend fun delete(email: String) {
        redisTemplate.delete(email)
    }
}
