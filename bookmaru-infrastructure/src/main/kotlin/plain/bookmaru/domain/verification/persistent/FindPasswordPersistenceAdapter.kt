package plain.bookmaru.domain.verification.persistent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.verification.port.out.FindPasswordPort
import java.time.Duration

@Component
class FindPasswordPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    @Qualifier("virtualDispatcher") private val virtualDispatcher: CoroutineDispatcher
) : FindPasswordPort {
    override suspend fun save(registerToken: String, email: String) = withContext(virtualDispatcher) {
        redisTemplate.opsForValue().set(
            email,
            registerToken,
            Duration.ofMinutes(10)
        )
    }

    override suspend fun load(email: String): String? = withContext(virtualDispatcher) {
        redisTemplate.opsForValue().get(email)
    }

    override suspend fun delete(email: String) {
        withContext(virtualDispatcher) { redisTemplate.delete(email) }
    }
}