package plain.bookmaru.domain.auth.presentation.dto.response

import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.PlatformType

data class TokenResponseDto(
    val username: String,
    val accessToken: String,
    val authority: Authority,
    val platformType: PlatformType,
    val affiliationName: String
) {
    companion object {
        fun toResponse(tokenResult: TokenResult) = TokenResponseDto(
            tokenResult.username,
            tokenResult.accessToken,
            tokenResult.authority,
            tokenResult.platformType,
            tokenResult.affiliationName
        )
    }
}
