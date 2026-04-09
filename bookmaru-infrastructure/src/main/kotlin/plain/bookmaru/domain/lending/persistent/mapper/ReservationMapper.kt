package plain.bookmaru.domain.lending.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.persistent.entity.BookReservationEntity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.domain.member.persistent.mapper.MemberMapper

@Component
class ReservationMapper(
    private val memberMapper: MemberMapper
) {

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