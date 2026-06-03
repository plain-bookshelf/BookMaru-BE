package plain.bookmaru.domain.auth.persistent

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.out.BlackListPort
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Component
class BlackListPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate
) : BlackListPort {

    override suspend fun save(accessToken: String, remainingTime: Long) {
        redisTemplate.opsForValue()
            .set(accessToken, "logout", remainingTime, TimeUnit.MILLISECONDS)

        log.info { "액세스 토큰을 블랙리스트에 등록했습니다." }
    }
}
