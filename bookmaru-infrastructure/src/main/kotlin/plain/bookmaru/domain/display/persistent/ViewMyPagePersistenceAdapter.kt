package plain.bookmaru.domain.display.persistent

import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.community.persistent.entity.QBookLikeEntity
import plain.bookmaru.domain.display.port.out.MyPagePort
import plain.bookmaru.domain.display.port.out.result.LendingBookListResult
import plain.bookmaru.domain.display.port.out.result.ViewMyPageLikeBookResult
import plain.bookmaru.domain.display.port.out.result.ViewMyPageResult
import plain.bookmaru.domain.inventory.persistent.entity.QBookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookDetailEntity
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.domain.lending.persistent.entity.QBookRentalRecordEntity
import plain.bookmaru.domain.lending.persistent.entity.QBookReservationEntity
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.persistent.entity.QMemberEntity
import plain.bookmaru.global.config.DbProtection
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Component
class ViewMyPagePersistenceAdapter(
    private val dbProtection: DbProtection,
    private val queryFactory: JPAQueryFactory
) : MyPagePort {
    private val bookAffiliation = QBookAffiliationEntity.bookAffiliationEntity
    private val bookDetail = QBookDetailEntity.bookDetailEntity
    private val bookRentalRecord = QBookRentalRecordEntity.bookRentalRecordEntity
    private val book = QBookEntity.bookEntity
    private val bookLike = QBookLikeEntity.bookLikeEntity
    private val member = QMemberEntity.memberEntity
    private val bookReservation = QBookReservationEntity.bookReservationEntity
    private val reservationRankSource = QBookReservationEntity("reservationRankSource")

    override suspend fun findUserInfoByUsername(username: String): ViewMyPageResult = dbProtection.withReadOnly {
        val today = LocalDate.now()

        val overdueCountSub = JPAExpressions
            .select(bookDetail.id.count().intValue())
            .from(bookDetail)
            .where(
                bookDetail.memberEntity.username.eq(username),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL),
                bookDetail.returnDate.lt(today)
            )

        val memberInfo = queryFactory
            .select(
                member.profileImage,
                member.nickname,
                member.rentalCount,
                member.reservationCount,
                overdueCountSub
            )
            .from(member)
            .where(member.username.eq(username))
            .fetchOne() ?: throw NotFoundMemberException("$username 아이디를 사용하는 유저를 찾지 못 했습니다.")

        val mostLittleLeftRentalBook = queryFactory
            .select(
                book.title,
                bookDetail.returnDate
            )
            .from(bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .where(
                bookDetail.memberEntity.username.eq(username),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL),
                bookDetail.returnDate.goe(today)
            )
            .orderBy(bookDetail.returnDate.asc())
            .limit(1)
            .fetchOne()

        val returnDate = mostLittleLeftRentalBook?.get(bookDetail.returnDate)
        val daysLeft = returnDate?.let { ChronoUnit.DAYS.between(today, it) } ?: 0

        return@withReadOnly ViewMyPageResult(
            profileImage = memberInfo.get(member.profileImage) ?: "",
            nickname = memberInfo.get(member.nickname) ?: username,
            mostLittleLeftRentalTitle = mostLittleLeftRentalBook?.get(book.title) ?: "",
            mostLittleLeftRentalDate = daysLeft.toInt(),
            rentedBookCount = memberInfo.get(member.rentalCount) ?: 0,
            reservedBookCount = memberInfo.get(member.reservationCount) ?: 0,
            overdueBookCount = memberInfo.get(overdueCountSub) ?: 0
        )
    }

    override suspend fun findUserLendingInfoByMemberId(memberId: Long): LendingBookListResult = dbProtection.withReadOnly {
        val today = LocalDate.now()
        val reservationDisplayRank = JPAExpressions
            .select(reservationRankSource.id.count().intValue())
            .from(reservationRankSource)
            .where(
                reservationRankSource.id.bookAffiliationId.eq(bookReservation.id.bookAffiliationId),
                reservationRankSource.waitingRank.loe(bookReservation.waitingRank)
            )

        queryFactory
            .selectOne()
            .from(member)
            .where(member.id.eq(memberId))
            .fetchFirst() ?: throw NotFoundMemberException("memberId: $memberId 유저를 찾지 못 했습니다.")

        val rentalBookList = queryFactory
            .select(
                bookAffiliation.id,
                book.bookImage,
                book.title,
                bookDetail.returnDate
            )
            .from(bookRentalRecord)
            .innerJoin(bookRentalRecord.bookDetailEntity, bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .where(
                bookDetail.memberEntity.id.eq(memberId),
                bookRentalRecord.returnDate.isNull.or(bookDetail.returnDate?.before(today))
            )
            .orderBy(bookDetail.returnDate.asc())
            .fetch()
            .map {
                val returnDate = it.get(bookDetail.returnDate)
                val leftDate = returnDate?.let { date -> ChronoUnit.DAYS.between(today, date) } ?: 0

                LendingBookListResult.RentalBookInfo(
                    bookAffiliationId = it.get(bookAffiliation.id) ?: 0L,
                    bookImage = it.get(book.bookImage) ?: "",
                    title = it.get(book.title) ?: "",
                    leftRentalDate = leftDate.toInt()
                )
            }

        val reservationBookList = queryFactory
            .select(
                bookAffiliation.id,
                book.bookImage,
                book.title,
                reservationDisplayRank
            )
            .from(bookReservation)
            .innerJoin(bookAffiliation)
                .on(bookReservation.id.bookAffiliationId.eq(bookAffiliation.id))
            .innerJoin(bookAffiliation.bookEntity, book)
            .where(
                bookReservation.id.memberId.eq(memberId)
            )
            .orderBy(bookReservation.waitingRank.asc())
            .fetch()
            .map {
                LendingBookListResult.ReservationBookInfo(
                    bookAffiliationId = it.get(bookAffiliation.id) ?: 0L,
                    bookImage = it.get(book.bookImage) ?: "",
                    title = it.get(book.title) ?: "",
                    rank = it.get(reservationDisplayRank) ?: 0
                )
            }

        val overdueBookList = queryFactory
            .select(
                bookAffiliation.id,
                book.bookImage,
                book.title,
                bookDetail.returnDate
            )
            .from(bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .where(
                bookDetail.memberEntity.id.eq(memberId),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL),
                bookDetail.returnDate.lt(today)
            )
            .orderBy(bookDetail.returnDate.asc())
            .fetch()
            .map {
                val returnDate = it.get(bookDetail.returnDate)
                val overdueDate = returnDate?.let { date -> ChronoUnit.DAYS.between(date, today) } ?: 0

                LendingBookListResult.OverDueBookInfo(
                    bookAffiliationId = it.get(bookAffiliation.id) ?: 0L,
                    bookImage = it.get(book.bookImage) ?: "",
                    title = it.get(book.title) ?: "",
                    overdueDate = overdueDate.toInt()
                )
            }

        return@withReadOnly LendingBookListResult(
            rentalBookInfo = rentalBookList,
            reservationBookInfo = reservationBookList,
            overDueBookInfo = overdueBookList
        )
    }

    override suspend fun findLikeBookByMemberId(memberId: Long): List<ViewMyPageLikeBookResult> = dbProtection.withReadOnly {
        return@withReadOnly queryFactory
            .from(bookLike)
            .join(bookLike.bookAffiliationEntity, bookAffiliation)
            .join(bookAffiliation.bookEntity, book)
            .join(bookLike.memberEntity, member)
            .where(
                member.id.eq(memberId)
            )
            .transform(
                groupBy(member.id).list(
                    Projections.constructor(
                        ViewMyPageLikeBookResult::class.java,
                        bookAffiliation.id,
                        book.title,
                        book.bookImage
                    )
                )
            )
    }
}
