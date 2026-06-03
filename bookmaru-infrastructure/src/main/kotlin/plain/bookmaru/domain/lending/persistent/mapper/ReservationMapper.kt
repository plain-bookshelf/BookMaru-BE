package plain.bookmaru.domain.lending.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.persistent.entity.BookReservationEntity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

@Component
class ReservationMapper{

    fun toDomain(entity: BookReservationEntity, memberProxy: Member) : Reservation {
        return Reservation(
            bookAffiliationId = entity.id!!.bookAffiliationId,
            member = memberProxy,
            waitingRank = entity.waitingRank
        )
    }

    fun toEntity(domain: Reservation, memberProxy: MemberEntity): BookReservationEntity {
        val embeddedId = BookReservationEmbeddedId(
            memberId = domain.member.id!!,
            bookAffiliationId = domain.bookAffiliationId
        )

        return BookReservationEntity(
            id = embeddedId,
            waitingRank = domain.waitingRank,
            memberEntity = memberProxy
        )
    }
}