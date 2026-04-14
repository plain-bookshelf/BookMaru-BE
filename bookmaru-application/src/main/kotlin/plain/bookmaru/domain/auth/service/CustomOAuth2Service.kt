package plain.bookmaru.domain.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.exception.AuthSessionExpiredException
import plain.bookmaru.domain.auth.exception.NotMatchPlatformInfoException
import plain.bookmaru.domain.auth.port.`in`.CustomOAuth2UseCase
import plain.bookmaru.domain.auth.port.`in`.SocialSignupUseCase
import plain.bookmaru.domain.auth.port.`in`.command.CustomOAuth2Command
import plain.bookmaru.domain.auth.port.`in`.command.SocialSignupCommand
import plain.bookmaru.domain.auth.port.out.JwtPort
import plain.bookmaru.domain.auth.port.out.OAuth2RegisterSessionPort
import plain.bookmaru.domain.auth.port.out.result.LoginResult
import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.domain.member.vo.LendingBook
import plain.bookmaru.domain.member.vo.Profile
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class CustomOAuth2Service(
    private val memberPort: MemberPort,
    private val jwtPort: JwtPort,
    private val oAuth2RegisterSessionPort: OAuth2RegisterSessionPort,
    private val affiliationPort: AffiliationPort,
    private val transactionPort: TransactionPort
) : CustomOAuth2UseCase, SocialSignupUseCase {

    override suspend fun execute(command: CustomOAuth2Command) : LoginResult {
        val targetEmail = command.email
        var member = memberPort.findByEmail(targetEmail.email)

        val provider = command.oAuthInfo.provider
        val providerId = command.oAuthInfo.providerId

        if (member != null) {
            member.linkOAuthAccount(provider, providerId)
            transactionPort.withTransaction {
                member = memberPort.save(member!!)
            }
            val tokens = jwtPort.responseToken(
                id = member.id!!,
                username = member.accountInfo!!.username,
                nickname = member.profile.nickname,
                platformType = command.platformType,
                authority = member.authority,
                affiliationId = member.affiliationId!!,
                oAuthProvider = provider,
                profileImage = member.profile.profileImage.toString()
            )

            log.info { "${member.accountInfo!!.username} 아이디로 로그인 완료" }
            return LoginResult.Success(tokens)
        } else {
            val registerToken = UUID.randomUUID().toString()
            val pendingUser = CustomOAuth2Command(
                platformType = command.platformType,
                oAuthInfo = command.oAuthInfo,
                email = targetEmail,
                nickname = command.nickname,
                profileImageUrl = command.profileImageUrl
            )

            oAuth2RegisterSessionPort.save(registerToken, pendingUser)
            log.info { "$targetEmail 이메일을 사용하여 소셜 로그인 접근" }
            return LoginResult.NeedMoreInfo(registerToken)
        }
    }

    override suspend fun execute(command: SocialSignupCommand) : TokenResult {
        val registerToken = command.registerToken
        val affiliationName = command.affiliationName

        val pendingUser = oAuth2RegisterSessionPort.getPendingUser(registerToken)
            ?: throw AuthSessionExpiredException("$registerToken 인증 세션 정보를 찾지 못 했습니다.")

        val affiliation = affiliationPort.findByAffiliationName(affiliationName)
            ?: throw NotFoundAffiliationException("$affiliationName 이름을 가진 소속 정보를 찾지 못 했습니다.")

        if (pendingUser.platformType != PlatformType.valueOf(command.platformType))
            throw NotMatchPlatformInfoException("${command.platformType} 정보가 registerToken 내에 있는 platform 정보와 일치하지 않습니다.")

        log.info { "provider : ${pendingUser.oAuthInfo.provider}" }
        log.info { "providerId : ${pendingUser.oAuthInfo.providerId}" }
        log.info { "email : ${pendingUser.email}" }
        log.info { "nickname : ${pendingUser.nickname}" }
        log.info { "profileImage Url : ${pendingUser.profileImageUrl}" }
        log.info { "affiliation_name : ${affiliation.affiliationName}" }

        val newMember = Member.createOAuthMember(
            oAuthInfo = pendingUser.oAuthInfo,
            email = pendingUser.email,
            affiliationId = affiliation.id,
            profile = Profile(nickname = pendingUser.nickname, profileImage = pendingUser.profileImageUrl),
            authority = Authority.ROLE_USER,
            lendingBook = LendingBook()
        )

        log.info { "$registerToken 를 통해서 유저 생성에 성공했습니다." }

        val savedMember = memberPort.save(newMember)

        oAuth2RegisterSessionPort.removePendingUser(registerToken)

        return jwtPort.responseToken(
            id = savedMember.id!!,
            username = savedMember.accountInfo!!.username,
            nickname = savedMember.profile.nickname,
            platformType = pendingUser.platformType,
            authority = savedMember.authority,
            affiliationId = savedMember.affiliationId!!,
            oAuthProvider = pendingUser.oAuthInfo.provider,
            profileImage = savedMember.profile.profileImage.toString()
        )
    }
}