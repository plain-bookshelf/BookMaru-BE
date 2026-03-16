package plain.bookmaru.domain.lending.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookRentalRecordEmbeddedId
import plain.bookmaru.global.entity.BaseEntity
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "book_rental_record")
class BookRentalRecordEntity(
    @EmbeddedId
    override val id: BookRentalRecordEmbeddedId,

    val rentalDate: LocalDateTime,

    var returnDate: LocalDate? = null
) : BaseEntity()