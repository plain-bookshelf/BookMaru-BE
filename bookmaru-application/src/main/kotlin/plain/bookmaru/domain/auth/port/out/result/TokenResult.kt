package plain.bookmaru.domain.auth.port.out.result

import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.auth.vo.PlatformType

data class TokenResult(
    val username: String,
    val accessToken: String,
    val accessTokenExpAt: Long,
    val refreshToken: String,
    val refreshTokenExpAt: Long,
    val authority: Authority,
    val platformType: PlatformType,
    val affiliationName: String,
    val oAuthProvider: OAuthProvider,
    val profileImage: String
)