package plain.bookmaru.domain.auth.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.PlatformType

@Aggregate
data class JwtRefreshToken(
    val sessionKey: String,
    val token: String,
    val username: String,
    val tokenExpire: Long,
    val authority: Authority,
    val platformType: PlatformType,
    val affiliationId: Long,
    val deviceToken: String? = null
) {

    fun update(token: String, tokenExpire: Long): JwtRefreshToken {
        return this.copy(
            token = token,
            tokenExpire = tokenExpire
        )
    }
}
