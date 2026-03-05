package plain.bookmaru.domain.verification.persistent

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.verification.port.out.FindPasswordPort
import plain.bookmaru.global.config.DbProtection
import java.time.Duration

@Component
class FindPasswordPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    private val dbProtection: DbProtection
) : FindPasswordPort {
    override suspend fun save(registerToken: String, username: String) = dbProtection.withTransaction {
        redisTemplate.opsForValue().set(
            username,
            registerToken,
            Duration.ofMinutes(10)
        )
    }

    override suspend fun load(username: String): String? = dbProtection.withReadOnly {
        redisTemplate.opsForValue().get(username)
    }

    override suspend fun delete(username: String) {
        dbProtection.withTransaction { redisTemplate.delete(username) }
    }
}