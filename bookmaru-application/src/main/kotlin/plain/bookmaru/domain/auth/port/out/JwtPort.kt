package plain.bookmaru.domain.auth.port.out

import plain.bookmaru.domain.affiliation.vo.Affiliation
import plain.bookmaru.domain.auth.result.TokenResult
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
        affiliation: Affiliation
    ): String

    suspend fun responseToken(
        id: Long,
        username: String,
        platformType: PlatformType,
        authority: Authority,
        affiliation: Affiliation
    ): TokenResult

}