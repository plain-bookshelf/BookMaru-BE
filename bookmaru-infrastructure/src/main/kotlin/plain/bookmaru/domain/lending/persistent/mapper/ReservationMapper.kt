package plain.bookmaru.domain.lending.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.persistent.entity.BookReservationEntity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId

@Component
class ReservationMapper {

    fun toEntity(domain: Reservation): BookReservationEntity {
        val embeddedId = BookReservationEmbeddedId(
            memberId = domain.memberId,
            bookAffiliationId = domain.bookAffiliationId
        )

        return BookReservationEntity(
            id = embeddedId,
            waitingRank = domain.waitingRank
        )
    }
}