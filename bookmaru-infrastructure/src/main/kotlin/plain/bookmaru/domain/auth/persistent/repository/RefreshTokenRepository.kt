package plain.bookmaru.domain.auth.persistent.repository

import org.springframework.data.repository.CrudRepository
import plain.bookmaru.domain.auth.persistent.entity.RefreshTokenEntity
import plain.bookmaru.domain.auth.vo.PlatformType

interface RefreshTokenRepository : CrudRepository<RefreshTokenEntity, String> {
    fun findByUsernameAndPlatformType(username: String, platformType: PlatformType): RefreshTokenEntity?
    fun findByTokenAndPlatformType(token: String, platformType: PlatformType): RefreshTokenEntity?
    fun deleteByUsername(username: String)
}