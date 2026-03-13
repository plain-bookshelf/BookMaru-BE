package plain.bookmaru.domain.lending.persistent.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookRentalRecordEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
class BookRentalRecordEntity(
    @Id
    override val id: BookRentalRecordEmbeddedId
) : BaseEntity() {
}