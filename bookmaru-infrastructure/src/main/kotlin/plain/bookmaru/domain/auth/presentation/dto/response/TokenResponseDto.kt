package plain.bookmaru.domain.auth.presentation.dto.response

import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.auth.vo.PlatformType

data class TokenResponseDto(
    val username: String,
    val nickname: String,
    val accessToken: String,
    val authority: Authority,
    val platformType: PlatformType,
    val affiliationName: String,
    val oAuthProvider: OAuthProvider,
    val profileImage: String,
    val refreshToken: String? = null
) {
    companion object {
        fun toWebResponse(tokenResult: TokenResult) = TokenResponseDto(
            username = tokenResult.username,
            nickname = tokenResult.nickname,
            accessToken = tokenResult.accessToken,
            authority = tokenResult.authority,
            platformType = tokenResult.platformType,
            affiliationName = tokenResult.affiliationName,
            oAuthProvider = tokenResult.oAuthProvider,
            profileImage = tokenResult.profileImage
        )

        fun toAppResponse(tokenResult: TokenResult) = TokenResponseDto(
            username = tokenResult.username,
            nickname = tokenResult.nickname,
            accessToken = tokenResult.accessToken,
            authority = tokenResult.authority,
            platformType = tokenResult.platformType,
            affiliationName = tokenResult.affiliationName,
            oAuthProvider = tokenResult.oAuthProvider,
            profileImage = tokenResult.profileImage,
            refreshToken = tokenResult.refreshToken
        )
    }
}
