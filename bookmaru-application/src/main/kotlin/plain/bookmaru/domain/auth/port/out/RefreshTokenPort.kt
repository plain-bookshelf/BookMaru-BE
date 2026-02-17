package plain.bookmaru.domain.auth.port.out

import plain.bookmaru.domain.auth.model.JwtRefreshToken
import plain.bookmaru.domain.auth.vo.PlatformType

interface RefreshTokenPort {
    suspend fun findByTokenAndPlatformType(token: String, platformType: PlatformType) : JwtRefreshToken?
    suspend fun deleteByUsername(username: String)
}