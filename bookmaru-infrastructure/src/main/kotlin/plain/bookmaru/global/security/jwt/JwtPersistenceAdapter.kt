package plain.bookmaru.global.security.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.persistent.RefreshTokenSessionKey
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

private val jwtLog = KotlinLogging.logger {}

@Component
class JwtPersistenceAdapter(
    private val jwtProperties: JwtProperties,
    private val refreshTokenMapper: RefreshTokenMapper,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val affiliationPort: AffiliationPort
) : JwtPort {

    suspend fun generateAccessToken(
        id: Long,
        username: String,
        authority: Authority,
        platformType: PlatformType,
        affiliationId: Long,
        oAuthProvider: OAuthProvider
    ): String = generateToken(
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
        oAuthProvider: OAuthProvider,
        deviceToken: String? = null
    ): String {
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

        val sessionKey = RefreshTokenSessionKey.of(username, platformType, deviceToken)
        val normalizedDeviceToken = deviceToken?.trim()?.takeIf { it.isNotEmpty() }
        val now = System.currentTimeMillis()
        val existingRefreshToken = refreshTokenRepository.findById(sessionKey).orElse(null)

        if (existingRefreshToken != null) {
            val updateEntity = refreshTokenMapper.toDomain(existingRefreshToken).update(
                token = token,
                tokenExpire = now + jwtProperties.refreshExp.toMillis()
            )
            refreshTokenRepository.save(refreshTokenMapper.toEntity(updateEntity))
        } else {
            refreshTokenRepository.save(
                RefreshTokenEntity(
                    sessionKey = sessionKey,
                    token = token,
                    username = username,
                    authority = authority,
                    platformType = platformType,
                    affiliationId = affiliationId,
                    deviceToken = normalizedDeviceToken,
                    tokenExpire = now + jwtProperties.refreshExp.toMillis()
                )
            )
        }

        return token
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
        jwtLog.info { "JWT 토큰 발급을 시작합니다." }

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
        profileImage: String,
        deviceToken: String?
    ): TokenResult {
        val accessToken = generateAccessToken(id, username, authority, platformType, affiliationId, oAuthProvider)
        jwtLog.info { "액세스 토큰 발급을 완료했습니다." }
        val refreshToken = generateRefreshToken(
            id,
            username,
            authority,
            platformType,
            affiliationId,
            oAuthProvider,
            deviceToken
        )
        jwtLog.info { "리프레시 토큰 발급을 완료했습니다." }

        val affiliation = affiliationPort.findById(affiliationId)
            ?: throw NotFoundAffiliationException("소속 정보를 찾을 수 없습니다.")

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
