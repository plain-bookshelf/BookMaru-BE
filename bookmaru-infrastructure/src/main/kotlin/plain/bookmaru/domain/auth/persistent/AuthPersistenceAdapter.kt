package plain.bookmaru.domain.auth.persistent

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.out.AuthPort
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Component
class AuthPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate
) : AuthPort {

    override suspend fun save(accessToken: String, remainingTime: Long) = withContext(Dispatchers.IO) {
        redisTemplate.opsForValue()
            .set(accessToken, "logout", remainingTime, TimeUnit.MILLISECONDS)

        log.info { "블랙리스트 등록 완료" }
    }
}