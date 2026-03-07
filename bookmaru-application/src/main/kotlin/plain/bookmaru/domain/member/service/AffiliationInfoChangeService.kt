package plain.bookmaru.domain.member.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.affiliation.exception.NotFoundAffiliationException
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.port.out.JwtPort
import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.`in`.AffiliationInfoChangeUseCase
import plain.bookmaru.domain.member.port.`in`.command.AffiliationInfoChangeCommand
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class AffiliationInfoChangeService(
    private val memberPort: MemberPort,
    private val affiliationPort: AffiliationPort,
    private val jwtPort: JwtPort
) : AffiliationInfoChangeUseCase {
    override suspend fun execute(command: AffiliationInfoChangeCommand): TokenResult {
        val username = command.username
        val affiliationName = command.affiliationName

        val member = memberPort.findByUsername(username)
            ?: throw NotFoundMemberException("$username 아이디를 사용하는 유저를 찾지 못 했습니다.")

        val affiliation = affiliationPort.findByAffiliationName(affiliationName)
            ?: throw NotFoundAffiliationException("$affiliationName 소속 정보가 존재하지 않습니다.")

        member.modifyAffiliation(affiliation.id!!)
        val savedMember = memberPort.save(member)

        log.info { "$username 유저가 소속 정보를 $affiliationName (으)로 변경하는 것을 성공했습니다." }

        return jwtPort.responseToken(
            id = savedMember.id!!,
            username = savedMember.accountInfo!!.username,
            platformType = command.platformType,
            authority = savedMember.authority,
            affiliationId = savedMember.affiliationId!!,
            oAuthProvider = savedMember.oAuthInfo!!.provider,
            profileImage = savedMember.profile.profileImage.toString()
        )
    }

}