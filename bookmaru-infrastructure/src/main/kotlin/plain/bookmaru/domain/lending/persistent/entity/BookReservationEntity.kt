package plain.bookmaru.domain.lending.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
class BookReservationEntity(
    @EmbeddedId
    override val id: BookReservationEmbeddedId? = null,

    var waitingRank: Int
) : BaseEntity()