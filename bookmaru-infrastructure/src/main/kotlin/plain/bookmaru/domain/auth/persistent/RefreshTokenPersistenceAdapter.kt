package plain.bookmaru.domain.auth.persistent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.model.JwtRefreshToken
import plain.bookmaru.domain.auth.persistent.mapper.RefreshTokenMapper
import plain.bookmaru.domain.auth.persistent.repository.RefreshTokenRepository
import plain.bookmaru.domain.auth.port.out.RefreshTokenPort
import plain.bookmaru.domain.auth.vo.PlatformType

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenMapper: RefreshTokenMapper,
    @Qualifier("virtualDispatcher") private val virtualDispatcher: CoroutineDispatcher
) : RefreshTokenPort {
    override suspend fun findByTokenAndPlatformType(
        token: String,
        platformType: PlatformType
    ) : JwtRefreshToken? = withContext(virtualDispatcher) {
        refreshTokenRepository.findByTokenAndPlatformType(token, platformType)?.let {
            refreshTokenMapper.toDomain(it)
        }
    }

    override suspend fun deleteByUsername(username: String) = withContext(virtualDispatcher) {
        refreshTokenRepository.deleteByUsername(username)
    }
}