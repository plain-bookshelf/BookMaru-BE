package plain.bookmaru.domain.auth.persistent

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.port.`in`.command.CustomOAuth2Command
import plain.bookmaru.domain.auth.port.out.OAuth2RegisterSessionPort
import plain.bookmaru.global.config.DbProtection
import java.time.Duration
import java.time.Instant

private val log = KotlinLogging.logger {}

@Component
class OAuth2RegisterSessionPersistenceAdapter(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
    private val dbProtection : DbProtection
) : OAuth2RegisterSessionPort {
    override suspend fun save(
        token: String,
        command: CustomOAuth2Command
    ) {
        val jsonData = objectMapper.writeValueAsString(command)

        redisTemplate.opsForValue().set(
            token,
            jsonData,
            Duration.between(Instant.now(), Instant.now().plusSeconds(60 * 30))
        )

        log.info { "registerToken 정보 저장 완료" }
    }

    override suspend fun getPendingUser(token: String): CustomOAuth2Command? = dbProtection.withReadOnly{
        val jsonData = redisTemplate.opsForValue().get(token) ?: return@withReadOnly null
        val codeData = objectMapper.readValue(jsonData, CustomOAuth2Command::class.java)

        return@withReadOnly CustomOAuth2Command(
            oAuthInfo = codeData.oAuthInfo,
            email = codeData.email,
            nickname = codeData.nickname,
            profileImageUrl = codeData.profileImageUrl,
            platformType = codeData.platformType
        )
    }

    override suspend fun removePendingUser(token: String) {
        val isDeleted = redisTemplate.delete(token)

        if (!isDeleted) log.warn { "$token 값을 지우지 못 했습니다." }
    }
}