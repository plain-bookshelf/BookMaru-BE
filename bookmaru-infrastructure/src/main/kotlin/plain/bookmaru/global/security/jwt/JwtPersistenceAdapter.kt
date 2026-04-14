package plain.bookmaru.global.security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.persistent.entity.RefreshTokenEntity
import plain.bookmaru.domain.auth.persistent.mapper.RefreshTokenMapper
import plain.bookmaru.domain.auth.persistent.repository.RefreshTokenRepository
import plain.bookmaru.domain.auth.port.out.JwtPort
import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.JwtType
import plain.bookmaru.domain.auth.vo.OAuthProvider
import plain.bookmaru.domain.auth.vo.PlatformType
import java.nio.charset.StandardCharsets
import java.util.Date

@Component
class JwtPersistenceAdapter(
    private val jwtProperties: JwtProperties,
    private val refreshTokenMapper: RefreshTokenMapper,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val affiliationPort: AffiliationPort
) : JwtPort{

    suspend fun generateAccessToken(
        id: Long,
        username: String,
        authority: Authority,
        platformType: PlatformType,
        affiliationId: Long,
        oAuthProvider: OAuthProvider
    ): String
    = generateToken(
        id,
        username,
        JwtType.ACCESS_TOKEN,
        jwtProperties.accessExp.toMillis(),
        platformType,
        authority,
        affiliationId,
        oAuthProvider
    )

    suspend fun generateRefreshToken(
        id: Long,
        username: String,
        authority: Authority,
        platformType: PlatformType,
        affiliationId: Long,
        oAuthProvider: OAuthProvider
    ): String = withContext(Dispatchers.IO){

        val token = generateToken(
            id,
            username,
            JwtType.REFRESH_TOKEN,
            jwtProperties.refreshExp.toMillis(),
            platformType,
            authority,
            affiliationId,
            oAuthProvider
        )

        val existingRefreshToken = refreshTokenRepository.findByUsernameAndPlatformType(username, platformType)

        val now = System.currentTimeMillis()

        if (existingRefreshToken != null) {
            val updateEntity = refreshTokenMapper.toDomain(existingRefreshToken).update(
                token = token,
                tokenExpire = now + jwtProperties.refreshExp.toMillis()
            )
            refreshTokenRepository.save(refreshTokenMapper.toEntity(updateEntity))
        } else {
            refreshTokenRepository.save(
                RefreshTokenEntity(
                    token = token,
                    username = username,
                    authority = authority,
                    platformType = platformType,
                    affiliationId = affiliationId,
                    tokenExpire = now + jwtProperties.refreshExp.toMillis(),
                )
            )
        }
        return@withContext token
    }

    override suspend fun generateToken(
        id: Long,
        username: String,
        tokenType: JwtType,
        exp: Long,
        platformType: PlatformType,
        authority: Authority,
        affiliationId: Long,
        oAuthProvider: OAuthProvider
    ): String {

        log.info { "토큰 발급 시도" }

        val now = System.currentTimeMillis()
        val key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))

        return Jwts.builder()
            .subject(username)
            .signWith(key, Jwts.SIG.HS256)
            .issuedAt(Date(now))
            .expiration(Date(now + exp))
            .claim(ClaimKey.MEMBER_ID.name, id)
            .claim(ClaimKey.TOKEN_TYPE.name, tokenType.name)
            .claim(ClaimKey.AUTHORITY.name, authority.name)
            .claim(ClaimKey.AFFILIATION.name, affiliationId)
            .claim(ClaimKey.OAUTH_PROVIDER.name, oAuthProvider.name)
        .compact()
    }

    override suspend fun responseToken(
        id: Long,
        username: String,
        nickname: String,
        platformType: PlatformType,
        authority: Authority,
        affiliationId: Long,
        oAuthProvider: OAuthProvider,
        profileImage: String
    ): TokenResult {

        val accessToken = generateAccessToken(id, username, authority, platformType, affiliationId, oAuthProvider)
        log.info { "AccessToken 발급에 성공했습니다." }
        val refreshToken = generateRefreshToken(id, username, authority, platformType, affiliationId, oAuthProvider)
        log.info { "RefreshToken 발급에 성공했습니다." }

        val affiliation = affiliationPort.findById(affiliationId)
            ?: throw NotFoundAffiliationException("소속 정보를 찾지 못 했습니다.")

        val now = System.currentTimeMillis()

        return TokenResult(
            username = username,
            nickname = nickname,
            accessToken = accessToken,
            accessTokenExpAt = now + jwtProperties.accessExp.toMillis(),
            refreshToken = refreshToken,
            refreshTokenExpAt = now + jwtProperties.refreshExp.toMillis(),
            authority = authority,
            platformType = platformType,
            affiliationName = affiliation.affiliationName.toString(),
            oAuthProvider = oAuthProvider,
            profileImage = profileImage
        )
    }
}