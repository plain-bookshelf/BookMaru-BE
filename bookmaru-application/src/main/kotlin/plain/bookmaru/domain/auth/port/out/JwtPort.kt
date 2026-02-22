package plain.bookmaru.domain.auth.port.out

import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.JwtType
import plain.bookmaru.domain.auth.vo.PlatformType

interface JwtPort {
    suspend fun generateToken(
        id: Long,
        username: String,
        tokenType: JwtType,
        exp: Long,
        platformType: PlatformType,
        authority: Authority,
        affiliationId: Long
    ): String

    suspend fun responseToken(
        id: Long,
        username: String,
        platformType: PlatformType,
        authority: Authority,
        affiliationId: Long
    ): TokenResult

}