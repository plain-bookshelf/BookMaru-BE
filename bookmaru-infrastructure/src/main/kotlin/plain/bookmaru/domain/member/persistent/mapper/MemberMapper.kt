package plain.bookmaru.domain.member.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.domain.member.vo.Profile

@Component
class MemberMapper{

    fun toDomain(entity: MemberEntity) : Member {
        return Member(
            id = entity.id,
            affiliationId = entity.affiliation.id!!,
            accountInfo = AccountInfo(entity.username, entity.password),
            profile = Profile(entity.nickname, entity.profileImage, entity.oneMonthStatics, entity.overdueTerm, entity.oftenBookReadTime),
            authority = entity.role,
            email = entity.email
        )
    }

    fun toEntity(domain: Member, affiliationProxy: AffiliationEntity) : MemberEntity {
        return MemberEntity(
            affiliation = affiliationProxy,
            username = domain.accountInfo?.username ?: domain.email.email.toString(),
            nickname = domain.profile.nickname,
            email = domain.email,
            role = domain.authority,
        ).apply {
            this.password = domain.accountInfo?.password ?: ""
            this.profileImage = domain.profile.profileImage ?: ""
            this.oftenBookReadTime = domain.profile.oftenBookReadTime
            this.overdueTerm = domain.profile.overdueTerm
            this.oneMonthStatics = domain.profile.oneMonthStatics ?: 0
        }
    }

    fun updateEntity(domain: Member, entity: MemberEntity, affiliationProxy: AffiliationEntity) {
        entity.affiliation = affiliationProxy
        entity.nickname = domain.profile.nickname
        entity.profileImage = domain.profile.profileImage ?: ""
        entity.oneMonthStatics = domain.profile.oneMonthStatics ?: 0
        entity.overdueTerm = domain.profile.overdueTerm
        entity.oftenBookReadTime = domain.profile.oftenBookReadTime
    }
}