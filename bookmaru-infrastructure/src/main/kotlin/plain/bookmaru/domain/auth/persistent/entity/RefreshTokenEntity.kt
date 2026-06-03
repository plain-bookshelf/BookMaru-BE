package plain.bookmaru.domain.auth.persistent.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.PlatformType

@RedisHash("refresh_token")
class RefreshTokenEntity(
    @Id
    val sessionKey: String,
    @Indexed
    val username: String,
    @Indexed
    val token: String,
    val authority: Authority,
    @Indexed
    val platformType: PlatformType,
    val affiliationId: Long,
    @Indexed
    val deviceToken: String? = null,
    @TimeToLive
    val tokenExpire: Long
)
