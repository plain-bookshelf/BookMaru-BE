package plain.bookmaru.domain.auth.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.model.JwtRefreshToken
import plain.bookmaru.domain.auth.persistent.entity.RefreshTokenEntity

@Component
class RefreshTokenMapper {

    fun toDomain(entity: RefreshTokenEntity) : JwtRefreshToken {
        return JwtRefreshToken(
            token = entity.token,
            username = entity.username,
            authority = entity.authority,
            platformType = entity.platformType,
            affiliationId = entity.affiliationId,
            tokenExpire = entity.tokenExpire,
        )
    }

    fun toEntity(domain: JwtRefreshToken) : RefreshTokenEntity {
        return RefreshTokenEntity(
            token = domain.token,
            username = domain.username,
            tokenExpire = domain.tokenExpire,
            authority = domain.authority,
            platformType = domain.platformType,
            affiliationId = domain.affiliationId
        )
    }
}