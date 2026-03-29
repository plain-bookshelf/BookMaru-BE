package plain.bookmaru.domain.lending.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.persistent.entity.QBookReservationEntity
import plain.bookmaru.domain.lending.persistent.mapper.ReservationMapper
import plain.bookmaru.domain.lending.persistent.repository.BookReservationRepository
import plain.bookmaru.domain.lending.port.out.BookReservationPort

@Component
class BookReservationPersistenceAdapter(
    private val bookReservationRepository: BookReservationRepository,
    private val queryFactory: JPAQueryFactory,
    private val reservationMapper: ReservationMapper
) : BookReservationPort {
    private val bookReservation = QBookReservationEntity.bookReservationEntity

    override suspend fun waiting(bookAffiliationId: Long): Int {
        val waitingRank = queryFactory
            .select(bookReservation.waitingRank.max())
            .from(bookReservation)
            .where(bookReservation.id.bookAffiliationId.eq(bookAffiliationId))
            .fetchOne()

        return (waitingRank ?: 0) + 1
    }

    override suspend fun save(reservation: Reservation) {
        val entity = reservationMapper.toEntity(reservation)

        bookReservationRepository.save(entity)
    }
}