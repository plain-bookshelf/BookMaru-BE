package plain.bookmaru.domain.auth.persistent.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed
import plain.bookmaru.domain.affiliation.model.Affiliation
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.PlatformType

@RedisHash
class RefreshTokenEntity(
    @Id
    val username: String,
    @Indexed
    val token: String,
    val authority: Authority,
    @Indexed
    val platformType: PlatformType,
    val affiliationId: Long,
    @TimeToLive
    val tokenExpire: Long
) {
}