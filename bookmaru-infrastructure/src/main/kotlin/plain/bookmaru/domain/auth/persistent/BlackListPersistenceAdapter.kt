package plain.bookmaru.domain.auth.persistent

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.out.BlackListPort
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Component
class BlackListPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    @Qualifier("virtualDispatcher") private val virtualDispatcher: CoroutineDispatcher
) : BlackListPort {

    override suspend fun save(accessToken: String, remainingTime: Long) = withContext(virtualDispatcher) {
        redisTemplate.opsForValue()
            .set(accessToken, "logout", remainingTime, TimeUnit.MILLISECONDS)

        log.info { "$accessToken 블랙리스트 등록 완료" }
    }
}