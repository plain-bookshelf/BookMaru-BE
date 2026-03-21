package plain.bookmaru.domain.lending.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.BookDetailEntity
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.vo.BookRecord
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import java.time.LocalDateTime

@Component
class RentalMapper {

    fun toDomain(entity: BookDetailEntity, rentalDate: LocalDateTime) : Rental {
        return Rental(
            memberId = entity.memberEntity.id,
            bookDetailId = entity.id!!,
            rentalStatus = entity.rentalStatus,
            bookRecord = BookRecord(
                rentalDate = rentalDate,
                returnDate = entity.returnDate!!
            ),
            rentalRequestStatus = entity.rentalRequestStatus,
        )
    }

    fun toEntity(
        domain: Rental,
        memberEntity: MemberEntity,
        bookAffiliationEntity: BookAffiliationEntity,
        bookDetailEntity: BookDetailEntity,
    ) : BookDetailEntity {
        return BookDetailEntity(
            memberEntity = memberEntity,
            bookAffiliationEntity = bookAffiliationEntity,
            registrationNumber = bookDetailEntity.registrationNumber,
            callNumber = bookDetailEntity.callNumber
        ).apply {
            this.rentalRequestStatus = domain.rentalRequestStatus
            this.rentalStatus = domain.rentalStatus
            this.returnDate = domain.bookRecord?.returnDate
        }
    }
}