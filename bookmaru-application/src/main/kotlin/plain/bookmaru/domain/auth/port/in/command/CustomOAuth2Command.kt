package plain.bookmaru.domain.auth.port.`in`.command

import com.fasterxml.jackson.annotation.JsonProperty
import plain.bookmaru.domain.auth.vo.OAuthInfo
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.vo.Email

data class CustomOAuth2Command(
    @JsonProperty("oauth_info")
    val oAuthInfo: OAuthInfo,
    val email: Email,
    val nickname: String,
    val profileImageUrl: String?,
    val platformType: PlatformType
)
