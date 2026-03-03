package plain.bookmaru.domain.auth.port.`in`.command

import plain.bookmaru.domain.auth.vo.OAuthInfo
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.vo.Email

data class CustomOAuth2Command(
    val oAuthInfo: OAuthInfo,
    val email: Email,
    val nickname: String,
    val profileImageUrl: String?,
    val platformType: PlatformType
)
