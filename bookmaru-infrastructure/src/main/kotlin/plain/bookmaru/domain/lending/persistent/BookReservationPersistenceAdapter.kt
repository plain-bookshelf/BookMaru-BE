package plain.bookmaru.domain.lending.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Component
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.persistent.entity.QBookReservationEntity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId
import plain.bookmaru.domain.lending.persistent.mapper.ReservationMapper
import plain.bookmaru.domain.lending.persistent.repository.BookReservationRepository
import plain.bookmaru.domain.lending.port.out.BookReservationPort
import plain.bookmaru.domain.member.persistent.mapper.MemberMapper
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.global.config.DbProtection

@Component
class BookReservationPersistenceAdapter(
    private val bookReservationRepository: BookReservationRepository,
    private val queryFactory: JPAQueryFactory,
    private val reservationMapper: ReservationMapper,
    private val memberMapper: MemberMapper,
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

    override suspend fun findFirstReservationByBookAffiliationId(bookAffiliationId: Long): Reservation? = dbProtection.withReadOnly {
        val reservationEntity = queryFactory
            .selectFrom(bookReservation)
            .where(bookReservation.id.bookAffiliationId.eq(bookAffiliationId))
            .orderBy(bookReservation.waitingRank.asc())
            .fetchFirst() ?: return@withReadOnly null

        val member = memberMapper.toDomain(memberRepository.getReferenceById(reservationEntity.memberEntity.id!!))

        return@withReadOnly reservationEntity.let { reservationMapper.toDomain(it, member) }
    }

    override fun findFirstReservationByBookAffiliationIdForUpdate(bookAffiliationId: Long): Reservation? {
        val reservationEntity = queryFactory
            .selectFrom(bookReservation)
            .where(bookReservation.id.bookAffiliationId.eq(bookAffiliationId))
            .orderBy(bookReservation.waitingRank.asc())
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchFirst() ?: return null

        val member = memberMapper.toDomain(memberRepository.getReferenceById(reservationEntity.memberEntity.id!!))

        return reservationMapper.toDomain(reservationEntity, member)
    }

    override suspend fun findReservation(bookAffiliationId: Long, memberId: Long): Reservation? = dbProtection.withReadOnly {
        val reservationEntity = queryFactory
            .selectFrom(bookReservation)
            .where(
                bookReservation.id.bookAffiliationId.eq(bookAffiliationId),
                bookReservation.id.memberId.eq(memberId)
            )
            .fetchFirst() ?: return@withReadOnly null

        val member = memberMapper.toDomain(memberRepository.getReferenceById(memberId))

        return@withReadOnly reservationMapper.toDomain(reservationEntity, member)
    }

    override fun save(reservation: Reservation) {
        val memberProxy = memberRepository.getReferenceById(reservation.member.id!!)

        val entity = reservationMapper.toEntity(reservation, memberProxy)

        bookReservationRepository.save(entity)
    }

    override fun deleteReservation(memberId: Long, bookAffiliationId: Long) {
        val embeddedId = BookReservationEmbeddedId(
            memberId = memberId,
            bookAffiliationId = bookAffiliationId
        )

        bookReservationRepository.deleteById(embeddedId)
    }
}
