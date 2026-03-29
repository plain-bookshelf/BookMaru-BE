package plain.bookmaru.domain.lending.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.lending.persistent.entity.BookReservationEntity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId

interface BookReservationRepository : JpaRepository<BookReservationEntity, BookReservationEmbeddedId> {
}