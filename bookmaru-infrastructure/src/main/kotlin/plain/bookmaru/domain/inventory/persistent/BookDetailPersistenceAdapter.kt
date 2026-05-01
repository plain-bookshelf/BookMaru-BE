package plain.bookmaru.domain.inventory.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.inventory.port.out.result.BookNotificationInfo
import plain.bookmaru.domain.inventory.persistent.entity.QBookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookDetailEntity
import plain.bookmaru.domain.inventory.persistent.mapper.BookDetailMapper
import plain.bookmaru.domain.inventory.persistent.repository.BookDetailRepository
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.manager.port.out.result.RentalBookStatusCheckResult
import plain.bookmaru.domain.member.persistent.entity.QMemberEntity
import plain.bookmaru.global.config.DbProtection
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

@Component
class BookDetailPersistenceAdapter(
    private val dbProtection: DbProtection,
    private val queryFactory: JPAQueryFactory,
    private val bookDetailRepository: BookDetailRepository,
    private val bookDetailMapper: BookDetailMapper
): BookDetailPort {
    private val member = QMemberEntity.memberEntity
    private val book = QBookEntity.bookEntity
    private val bookDetail = QBookDetailEntity.bookDetailEntity
    private val bookAffiliation = QBookAffiliationEntity.bookAffiliationEntity
    private val affiliation = QAffiliationEntity.affiliationEntity

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

    override suspend fun findRentalBookStatusCheckByAffiliationId(
        command: PageCommand,
        affiliationId: Long
    ): PageResult<RentalBookStatusCheckResult>? = dbProtection.withReadOnly {
        val contentTuple = queryFactory
            .select(
                member.id,
                bookDetail.id,
                book.title,
                book.publisher,
                member.nickname,
                bookDetail.callNumber,
                bookDetail.returnDate
            )
            .from(bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookDetail.memberEntity, member)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .where(
                affiliation.id.eq(affiliationId),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL)
            )
            .orderBy(bookDetail.returnDate.asc())
            .offset(command.offset)
            .limit(command.size.toLong())
            .fetch()

        val totalElements = queryFactory
            .select(bookDetail.count())
            .from(bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .where(
                affiliation.id.eq(affiliationId),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL)
            )
            .fetchOne() ?: 0L

        val content = mapToRentalBookStatusCheckResult(contentTuple)
        return@withReadOnly createPageResult(content, totalElements, command)
    }

    override suspend fun findRentalBookStatusCheckByAffiliationIdAndNickname(
        command: PageCommand,
        affiliationId: Long,
        nickname: String
    ): PageResult<RentalBookStatusCheckResult>? = dbProtection.withReadOnly {
        val contentTuple = queryFactory
            .select(
                member.id,
                bookDetail.id,
                book.title,
                book.publisher,
                member.nickname,
                bookDetail.callNumber,
                bookDetail.returnDate
            )
            .from(bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookDetail.memberEntity, member)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .where(
                affiliation.id.eq(affiliationId),
                member.nickname.eq(nickname),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL)
            )
            .orderBy(bookDetail.returnDate.asc())
            .offset(command.offset)
            .limit(command.size.toLong())
            .fetch()

        val totalElements = queryFactory
            .select(bookDetail.count())
            .from(bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .innerJoin(bookDetail.memberEntity, member)
            .where(
                affiliation.id.eq(affiliationId),
                member.nickname.eq(nickname),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL)
            )
            .fetchOne() ?: 0L

        val content = mapToRentalBookStatusCheckResult(contentTuple)
        return@withReadOnly createPageResult(content, totalElements, command)
    }

    override suspend fun findRentalBookByBookDetailId(bookDetailId: Long): BookDetail? = dbProtection.withReadOnly {
        return@withReadOnly bookDetailRepository.findByIdOrNull(bookDetailId)
            ?.let { bookDetailMapper.toDomain(it) }
    }

    override suspend fun findBookNotificationInfoByBookDetailId(bookDetailId: Long): BookNotificationInfo? = dbProtection.withReadOnly {
        return@withReadOnly queryFactory
            .select(
                com.querydsl.core.types.Projections.constructor(
                    BookNotificationInfo::class.java,
                    bookAffiliation.id,
                    book.title
                )
            )
            .from(bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .where(bookDetail.id.eq(bookDetailId))
            .fetchOne()
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

    override fun approveRentalRequest(bookDetailId: Long): Long {
        return queryFactory.update(bookDetail)
            .set(bookDetail.rentalStatus, RentalStatus.RENTAL)
            .where(
                bookDetail.id.eq(bookDetailId),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL_REQUEST)
            )
            .execute()
    }

    override fun assignReturnedRental(bookDetailId: Long, memberId: Long, returnDate: LocalDate) {
        queryFactory.update(bookDetail)
            .set(bookDetail.rentalStatus, RentalStatus.RENTAL)
            .set(bookDetail.memberEntity.id, memberId)
            .set(bookDetail.returnDate, returnDate)
            .where(
                bookDetail.id.eq(bookDetailId),
                bookDetail.rentalStatus.eq(RentalStatus.RETURN)
            )
            .execute()
    }

    /*
    private helper method
     */

    private fun mapToRentalBookStatusCheckResult(contentTuple: List<com.querydsl.core.Tuple>): List<RentalBookStatusCheckResult> {
        val today = LocalDate.now()

        return contentTuple.map {
            val returnDate = it.get(bookDetail.returnDate)
            val daysLeft = returnDate?.let { date -> ChronoUnit.DAYS.between(today, date) } ?: 0

            RentalBookStatusCheckResult(
                memberId = it.get(member.id) ?: 0L,
                bookDetailId = it.get(bookDetail.id) ?: 0L,
                title = it.get(book.title) ?: "",
                publisher = it.get(book.publisher) ?: "",
                nickname = it.get(member.nickname) ?: "",
                callNumber = it.get(bookDetail.callNumber) ?: "",
                returnDate = returnDate ?: today,
                isOverdue = daysLeft < 0
            )
        }
    }

    private fun <T> createPageResult(
        content: List<T>,
        totalElements: Long,
        command: PageCommand
    ): PageResult<T> {
        val totalPages = ceil(totalElements.toDouble() / command.size).toInt()
        val isLastPage = (command.page + 1) >= totalPages

        return PageResult(
            content = content,
            totalElements = totalElements,
            totalPages = totalPages,
            isLastPage = if (totalElements == 0L) true else isLastPage
        )
    }
}
