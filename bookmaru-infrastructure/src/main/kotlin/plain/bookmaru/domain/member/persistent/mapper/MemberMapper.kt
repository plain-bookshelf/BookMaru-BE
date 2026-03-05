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
            profile = Profile(entity.nickname, entity.profileImage, entity.oneMonthStatics, entity.overdueTerm, entity.bookReadTime),
            authority = entity.role,
            email = entity.email
        )
    }

    fun toEntity(domain: Member, affiliationProxy: AffiliationEntity) : MemberEntity {
        return MemberEntity(
            id = domain.id,
            affiliation = affiliationProxy,
            username = domain.accountInfo?.username ?: domain.email!!.email.toString(),
            nickname = domain.profile.nickname,
            password = domain.accountInfo?.password.toString(),
            email = domain.email,
            role = domain.authority,
            profileImage = domain.profile.profileImage ?: "",
            oneMonthStatics = domain.profile.oneMonthStatics,
            overdueTerm = domain.profile.overdueTerm,
            bookReadTime = domain.profile.oftenBookReadTime,
        )
    }
}