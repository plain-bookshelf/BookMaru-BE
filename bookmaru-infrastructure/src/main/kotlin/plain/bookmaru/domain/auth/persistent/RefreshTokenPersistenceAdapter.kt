package plain.bookmaru.domain.auth.persistent

import org.springframework.stereotype.Component
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
    ) = dbProtection.withReadOnly {
        refreshTokenRepository.findByTokenAndPlatformType(token, platformType)?.let {
            refreshTokenMapper.toDomain(it)
        }
    }

    override suspend fun deleteByUsername(username: String) = dbProtection.withTransaction {
        val sessions = refreshTokenRepository.findAllByUsername(username)
        if (sessions.isNotEmpty()) {
            refreshTokenRepository.deleteAll(sessions)
        }
    }

    override suspend fun deleteCurrentSession(username: String, platformType: PlatformType, deviceToken: String?) =
        dbProtection.withTransaction {
            refreshTokenRepository.deleteById(RefreshTokenSessionKey.of(username, platformType, deviceToken))
        }
}
