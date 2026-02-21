package plain.bookmaru.domain.member.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.model.Affiliation
import plain.bookmaru.domain.affiliation.persistent.mapper.AffiliationMapper
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.domain.member.vo.Profile

@Component
class MemberMapper(
    private val affiliationMapper: AffiliationMapper
) {

    fun toDomain(entity: MemberEntity) : Member {
        return Member(
            id = entity.id,
            affiliationId = entity.affiliation.id!!,
            accountInfo = AccountInfo(entity.username, entity.password),
            profile = Profile(entity.nickname, entity.profileImage, entity.oneMonthStatics, entity.overdueTerm, entity.bookReadTime),
            authority = entity.role,
            email = entity.email
        )
    }

    fun toEntity(domain: Member, affiliation: Affiliation) : MemberEntity {
        return MemberEntity(
            id = domain.id,
            affiliation = affiliationMapper.toEntity(affiliation),
            username = domain.accountInfo.username,
            nickname = domain.profile?.nickname ?: domain.accountInfo.username,
            password = domain.accountInfo.password,
            email = domain.email,
            role = domain.authority,
            profileImage = domain.profile?.profileImage ?: "",
            oneMonthStatics = domain.profile?.oneMonthStatics,
            overdueTerm = domain.profile?.overdueTerm,
            bookReadTime = domain.profile?.bookReadTime,
        )
    }
}