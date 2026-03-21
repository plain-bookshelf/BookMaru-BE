package plain.bookmaru.domain.lending.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(name = "book_reservation")
class BookReservationEntity(
    @EmbeddedId
    override val id: BookReservationEmbeddedId? = null,

    var waitingRank: Int
) : BaseEntity()