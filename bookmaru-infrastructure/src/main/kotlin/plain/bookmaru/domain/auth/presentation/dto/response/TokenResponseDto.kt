package plain.bookmaru.domain.auth.presentation.dto.response

import plain.bookmaru.domain.affiliation.vo.Affiliation
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.PlatformType

data class TokenResponseDto(
    val accessToken: String,
    val accessTokenExpAt: Long,
    val refreshToken: String,
    val refreshTokenExpAt: Long,
    val authority: Authority,
    val platformType: PlatformType,
    val affiliation: Affiliation
)
