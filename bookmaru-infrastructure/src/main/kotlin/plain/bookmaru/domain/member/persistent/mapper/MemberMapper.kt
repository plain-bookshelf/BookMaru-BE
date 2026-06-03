package plain.bookmaru.domain.member.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.auth.vo.AccountInfo
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.member.vo.LendingBook
import plain.bookmaru.domain.member.vo.Profile

@Component
class MemberMapper{

    fun toDomain(entity: MemberEntity) : Member {
        return Member(
            id = entity.id,
            affiliationId = entity.affiliationEntity.id!!,
            accountInfo = AccountInfo(entity.username, entity.password),
            profile = Profile(
                entity.nickname,
                entity.profileImage,
                entity.oneMonthStatistics,
                entity.overdueTerm,
                entity.oftenBookReadTime,
                entity.overdueStatus,
                entity.deleteStatus
            ),
            authority = entity.role,
            email = Email(email = entity.email),
            lendingBook = LendingBook(entity.rentalCount, entity.reservationCount)
        )
    }

    fun toEntity(domain: Member, affiliationProxy: AffiliationEntity) : MemberEntity {
        return MemberEntity(
            affiliationEntity = affiliationProxy,
            password = domain.accountInfo?.password,
            username = domain.accountInfo?.username ?: domain.email.email,
            nickname = domain.profile.nickname,
            email = domain.email.email,
            role = domain.authority,
            deleteStatus = domain.profile.deleteStatus
        )
    }

    fun toDomainList(entities: List<MemberEntity>) : List<Member> {
        return entities.map { toDomain(it) }
    }

    fun updateEntity(domain: Member, entity: MemberEntity, affiliationProxy: AffiliationEntity) {
        entity.affiliationEntity = affiliationProxy
        entity.username = domain.accountInfo?.username ?: entity.username
        entity.password = domain.accountInfo?.password ?: entity.password
        entity.email = domain.email.email
        entity.nickname = domain.profile.nickname
        entity.profileImage = domain.profile.profileImage ?: entity.profileImage
        entity.oneMonthStatistics = domain.profile.oneMonthStatistics ?: entity.oneMonthStatistics
        entity.overdueTerm = domain.profile.overdueTerm
        entity.overdueStatus = domain.profile.overdueStatus
        entity.oftenBookReadTime = domain.profile.oftenBookReadTime
        entity.rentalCount = domain.lendingBook.rentalCount
        entity.reservationCount = domain.lendingBook.reservationCount
        entity.deleteStatus = domain.profile.deleteStatus
    }
}
