package plain.bookmaru.domain.inventory.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.inventory.persistent.entity.QBookDetailEntity
import plain.bookmaru.domain.inventory.persistent.mapper.BookDetailMapper
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.global.config.DbProtection
import java.time.LocalDate

@Component
class BookDetailPersistenceAdapter(
    private val dbProtection: DbProtection,
    private val queryFactory: JPAQueryFactory,
    private val bookDetailMapper: BookDetailMapper
): BookDetailPort {
    private val bookDetail = QBookDetailEntity.bookDetailEntity

    override suspend fun findRentalBookDetailByBookAffiliationId(
        bookAffiliationId: Long
    ): BookDetail? = dbProtection.withReadOnly {
        val bookDetailEntity = queryFactory
            .selectFrom(bookDetail)
            .where(
                bookDetail.bookAffiliationEntity.id.eq(bookAffiliationId),
                bookDetail.rentalStatus.eq(RentalStatus.RETURN)
            )
            .fetchFirst()

        return@withReadOnly bookDetailEntity?.let { bookDetailMapper.toDomain(it) }
    }

    override fun updateRental(rental: Rental, returnDate: LocalDate) {
        queryFactory.update(bookDetail)
            .set(bookDetail.rentalStatus, RentalStatus.RENTAL_REQUEST)
            .set(bookDetail.memberEntity.id, rental.memberId)
            .set(bookDetail.returnDate, returnDate)
            .where(
                bookDetail.id.eq(rental.bookDetailId),
                bookDetail.rentalStatus.eq(RentalStatus.RETURN)
            )
            .execute()
    }
}