package plain.bookmaru.domain.auth.persistent

import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.model.JwtRefreshToken
import plain.bookmaru.domain.auth.persistent.mapper.RefreshTokenMapper
import plain.bookmaru.domain.auth.persistent.repository.RefreshTokenRepository
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.global.config.DbProtection

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenMapper: RefreshTokenMapper,
    private val dbProtection: DbProtection
) : RefreshTokenPort {
    override suspend fun findByTokenAndPlatformType(
        token: String,
        platformType: PlatformType
    ) : JwtRefreshToken? = dbProtection.withReadOnly {
        refreshTokenRepository.findByTokenAndPlatformType(token, platformType)?.let {
            refreshTokenMapper.toDomain(it)
        }
    }

    override suspend fun deleteByUsername(username: String) = dbProtection.withTransaction {
        refreshTokenRepository.deleteByUsername(username)
    }
}