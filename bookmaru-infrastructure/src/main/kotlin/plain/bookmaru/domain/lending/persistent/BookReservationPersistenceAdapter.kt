package plain.bookmaru.domain.lending.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.persistent.entity.QBookReservationEntity
import plain.bookmaru.domain.lending.persistent.mapper.ReservationMapper
import plain.bookmaru.domain.lending.persistent.repository.BookReservationRepository
import plain.bookmaru.domain.lending.port.out.BookReservationPort
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.global.config.DbProtection

@Component
class BookReservationPersistenceAdapter(
    private val bookReservationRepository: BookReservationRepository,
    private val queryFactory: JPAQueryFactory,
    private val reservationMapper: ReservationMapper,
    private val memberRepository: MemberRepository,
    private val dbProtection: DbProtection
) : BookReservationPort {
    private val bookReservation = QBookReservationEntity.bookReservationEntity

    override suspend fun waiting(bookAffiliationId: Long): Int = dbProtection.withReadOnly {
        val waitingRank = queryFactory
            .select(bookReservation.waitingRank.max())
            .from(bookReservation)
            .where(bookReservation.id.bookAffiliationId.eq(bookAffiliationId))
            .fetchOne()

        return@withReadOnly (waitingRank ?: 0) + 1
    }

    override fun save(reservation: Reservation) {
        val memberProxy = memberRepository.getReferenceById(reservation.member.id!!)

        val entity = reservationMapper.toEntity(reservation, memberProxy)

        bookReservationRepository.save(entity)
    }
}