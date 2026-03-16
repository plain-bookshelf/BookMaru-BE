package plain.bookmaru.domain.lending.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(
    name = "book_reservation",
    indexes = [
        Index(name = "idx_book_reservation_id", columnList = "book_detail_id"),
        Index(name = "idx_member_id", columnList = "member_id")
    ]
)
class BookReservationEntity(
    @EmbeddedId
    override val id: BookReservationEmbeddedId? = null,

    var waitingRank: Int
) : BaseEntity()