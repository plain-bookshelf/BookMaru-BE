package plain.bookmaru.domain.auth.persistent

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
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
    private val objectMapper: ObjectMapper
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

        log.info { "OAuth2 회원가입 대기 세션을 저장했습니다." }
    }

    override suspend fun getPendingUser(token: String): CustomOAuth2Command? {
        val jsonData = redisTemplate.opsForValue().get(OAUTH_TOKEN_KEY + token) ?: return null
        val codeData = objectMapper.readValue(jsonData, CustomOAuth2Command::class.java)

        return CustomOAuth2Command(
            oAuthInfo = codeData.oAuthInfo,
            email = codeData.email,
            nickname = codeData.nickname,
            profileImageUrl = codeData.profileImageUrl,
            platformType = codeData.platformType
        )
    }

    override suspend fun removePendingUser(token: String) {
        val isDeleted = redisTemplate.delete(OAUTH_TOKEN_KEY + token)

        if (!isDeleted) {
            log.warn { "삭제할 OAuth2 회원가입 대기 세션이 없습니다." }
        }
    }
}
