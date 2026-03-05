package plain.bookmaru.domain.auth.persistent

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.out.BlackListPort
import plain.bookmaru.global.config.DbProtection
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Component
class BlackListPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    private val dbProtection: DbProtection
) : BlackListPort {

    override suspend fun save(accessToken: String, remainingTime: Long) = dbProtection.withTransaction {
        redisTemplate.opsForValue()
            .set(accessToken, "logout", remainingTime, TimeUnit.MILLISECONDS)

        log.info { "$accessToken 블랙리스트 등록 완료" }
    }
}