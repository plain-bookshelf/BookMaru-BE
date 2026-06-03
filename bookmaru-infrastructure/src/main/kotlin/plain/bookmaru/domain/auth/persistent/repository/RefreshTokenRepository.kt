package plain.bookmaru.domain.auth.persistent.repository

import org.springframework.data.repository.CrudRepository
import plain.bookmaru.domain.auth.persistent.entity.RefreshTokenEntity
import plain.bookmaru.domain.auth.vo.PlatformType

interface RefreshTokenRepository : CrudRepository<RefreshTokenEntity, String> {
    fun findByTokenAndPlatformType(token: String, platformType: PlatformType): RefreshTokenEntity?
    fun findAllByUsername(username: String): List<RefreshTokenEntity>
}
