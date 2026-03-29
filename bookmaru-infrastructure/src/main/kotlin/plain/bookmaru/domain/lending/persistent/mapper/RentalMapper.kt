package plain.bookmaru.domain.lending.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.inventory.persistent.entity.BookDetailEntity
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.persistent.entity.BookRentalRecordEntity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookRentalRecordEmbeddedId
import plain.bookmaru.domain.lending.vo.BookRecord
import java.time.LocalDateTime

@Component
class RentalMapper {

    fun toDomain(entity: BookDetailEntity, rentalDate: LocalDateTime) : Rental {
        return Rental(
            memberId = entity.memberEntity.id!!,
            bookDetailId = entity.id!!,
            bookRecord = BookRecord(
                rentalDate = rentalDate,
                returnDate = entity.returnDate
            )
        )
    }

    fun toEntity(
        domain: Rental
    ) : BookRentalRecordEntity {
        val embeddedId = BookRentalRecordEmbeddedId(
            memberId = domain.memberId,
            bookDetailId = domain.bookDetailId
        )

        return BookRentalRecordEntity(
            id = embeddedId,
            rentalDate = domain.bookRecord.rentalDate
        ).apply {
            this.returnDate = domain.bookRecord.returnDate
        }
    }
}