package plain.bookmaru.domain.auth.persistent

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.`in`.command.CustomOAuth2Command
import plain.bookmaru.domain.auth.port.out.OAuth2RegisterSessionPort
import java.time.Duration
import java.time.Instant

private val log = KotlinLogging.logger {}

private const val OAUTH_TOKEN_KEY = "auth:oauth_token:"

@Component
class OAuth2RegisterSessionPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    @Qualifier("virtualDispatcher") private val virtualDispatcher: CoroutineDispatcher
) : OAuth2RegisterSessionPort {
    override suspend fun save(
        token: String,
        command: CustomOAuth2Command
    ) {
        val jsonData = objectMapper.writeValueAsString(command)

        redisTemplate.opsForValue().set(
            OAUTH_TOKEN_KEY + token,
            jsonData,
            Duration.between(Instant.now(), Instant.now().plusSeconds(60 * 30))
        )

        log.info { "registerToken 정보 저장 완료" }
    }

    override suspend fun getPendingUser(token: String): CustomOAuth2Command? = withContext(virtualDispatcher) {
        val jsonData = redisTemplate.opsForValue().get(OAUTH_TOKEN_KEY + token) ?: return@withContext null
        val codeData = objectMapper.readValue(jsonData, CustomOAuth2Command::class.java)

        return@withContext CustomOAuth2Command(
            oAuthInfo = codeData.oAuthInfo,
            email = codeData.email,
            nickname = codeData.nickname,
            profileImageUrl = codeData.profileImageUrl,
            platformType = codeData.platformType
        )
    }

    override suspend fun removePendingUser(token: String) = withContext(virtualDispatcher) {
        val isDeleted = redisTemplate.delete(OAUTH_TOKEN_KEY + token)

        if (!isDeleted) log.warn { "$token 값을 지우지 못 했습니다." }
    }
}