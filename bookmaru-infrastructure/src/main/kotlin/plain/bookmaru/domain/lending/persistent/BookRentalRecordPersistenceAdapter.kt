package plain.bookmaru.domain.lending.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookDetailEntity.bookDetailEntity
import plain.bookmaru.domain.inventory.persistent.repository.BookDetailRepository
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.domain.lending.exception.NotFoundRentalRecordException
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.persistent.entity.QBookRentalRecordEntity
import plain.bookmaru.domain.lending.persistent.mapper.RentalMapper
import plain.bookmaru.domain.lending.persistent.repository.BookRentalRecordRepository
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.lending.port.out.result.AppRentalRequestCheckResult
import plain.bookmaru.domain.lending.port.out.result.OverdueNotificationTarget
import plain.bookmaru.domain.lending.port.out.result.RentalRequestApprovalInfo
import plain.bookmaru.domain.lending.port.out.result.RentalRequestCheckResult
import plain.bookmaru.domain.lending.port.out.result.WebRentalRequestCheckResult
import plain.bookmaru.domain.member.persistent.entity.QMemberEntity
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.global.config.DbProtection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class BookRentalRecordPersistenceAdapter(
    private val bookRentalRecordRepository: BookRentalRecordRepository,
    private val bookDetailRepository: BookDetailRepository,
    private val memberRepository: MemberRepository,
    private val rentalMapper: RentalMapper,
    private val queryFactory: JPAQueryFactory,
    private val dbProtection: DbProtection
) : BookRentalRecordPort {
    private val member = QMemberEntity.memberEntity
    private val book = QBookEntity.bookEntity
    private val bookDetail = bookDetailEntity
    private val bookAffiliation = QBookAffiliationEntity.bookAffiliationEntity
    private val affiliation = QAffiliationEntity.affiliationEntity
    private val bookRentalRecord = QBookRentalRecordEntity.bookRentalRecordEntity

    override fun save(renter: Rental) {
        val memberProxy = memberRepository.getReferenceById(renter.memberId)
        val bookDetailProxy = bookDetailRepository.getReferenceById(renter.bookDetailId)
        val rentalEntity = rentalMapper.toEntity(renter, memberProxy, bookDetailProxy)
        bookRentalRecordRepository.save(rentalEntity)
    }

    override fun completeReturn(bookDetailId: Long) {
        val now = LocalDate.now()
        val currentDateTime = LocalDateTime.now()

        val recordEntity = queryFactory
            .selectFrom(bookRentalRecord)
            .join(bookRentalRecord.bookDetailEntity, bookDetail).fetchJoin()
            .join(bookRentalRecord.memberEntity, member).fetchJoin()
            .where(
                bookRentalRecord.bookDetailEntity.id.eq(bookDetailId),
                bookRentalRecord.returnDate.isNull
            )
            .fetchOne() ?: throw NotFoundRentalRecordException("해당 책의 진행 중인 대여 기록을 찾지 못했습니다.")

        val detailEntity = recordEntity.bookDetailEntity
        val memberEntity = recordEntity.memberEntity
        val expectedReturnDate = detailEntity.returnDate

        if (expectedReturnDate != null && now.isAfter(expectedReturnDate)) {
            val overdueDays = ChronoUnit.DAYS.between(expectedReturnDate, now)

            memberEntity.let {
                it.overdueStatus = true

                val existingOverdueTerm = it.overdueTerm
                if (existingOverdueTerm != null && existingOverdueTerm.isAfter(currentDateTime)) {
                    it.overdueTerm = existingOverdueTerm.plusDays(overdueDays)
                } else {
                    it.overdueTerm = currentDateTime.plusDays(overdueDays)
                }
            }
        }

        memberEntity.rentalCount = (memberEntity.rentalCount - 1).coerceAtLeast(0)

        recordEntity.returnDate = now

        detailEntity.rentalStatus = RentalStatus.RETURN
        detailEntity.memberEntity = null
        detailEntity.returnDate = null
    }

    override suspend fun findRentalRequestBookByAffiliationId(
        affiliationId: Long
    ): List<RentalRequestCheckResult> {
        return findRentalRequestBookByAffiliationIdForPlatform(affiliationId, PlatformType.ANDROID)
    }

    override suspend fun findRentalRequestBookByAffiliationIdForPlatform(
        affiliationId: Long,
        platformType: PlatformType
    ): List<RentalRequestCheckResult> = dbProtection.withReadOnly {
        val contentTuple = queryFactory
            .select(
                bookDetail.id,
                member.id,
                member.nickname,
                book.title,
                bookDetail.callNumber,
                bookRentalRecord.rentalDate
            )
            .from(bookRentalRecord)
            .innerJoin(bookRentalRecord.bookDetailEntity, bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .innerJoin(bookRentalRecord.memberEntity, member)
            .where(
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL_REQUEST),
                affiliation.id.eq(affiliationId)
            )
            .orderBy(book.id.desc())
            .fetch()

        return@withReadOnly contentTuple.map {
            if (platformType == PlatformType.WEB) {
                WebRentalRequestCheckResult(
                    bookDetailId = it.get(bookDetail.id) ?: 0L,
                    memberId = it.get(member.id) ?: 0L,
                    nickName = it.get(member.nickname) ?: "",
                    title = it.get(book.title) ?: "",
                    callNumber = it.get(bookDetail.callNumber) ?: "",
                    rentalRequestDate = requireNotNull(it.get(bookRentalRecord.rentalDate)) {
                        "rentalRequestDate must exist in rental record."
                    }
                )
            } else {
                AppRentalRequestCheckResult(
                    bookDetailId = it.get(bookDetail.id) ?: 0L,
                    memberId = it.get(member.id) ?: 0L,
                    nickName = it.get(member.nickname) ?: "",
                    title = it.get(book.title) ?: "",
                    callNumber = it.get(bookDetail.callNumber) ?: ""
                )
            }
        }
    }

    override suspend fun findRentalRequestApprovalInfo(
        bookDetailId: Long,
        affiliationId: Long
    ): RentalRequestApprovalInfo? = dbProtection.withReadOnly {
        return@withReadOnly queryFactory
            .select(
                Projections.constructor(
                    RentalRequestApprovalInfo::class.java,
                    member.id,
                    bookDetail.id,
                    bookAffiliation.id,
                    book.title,
                    book.bookImage.coalesce(""),
                    bookDetail.returnDate
                )
            )
            .from(bookRentalRecord)
            .innerJoin(bookRentalRecord.bookDetailEntity, bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .innerJoin(bookRentalRecord.memberEntity, member)
            .where(
                bookDetail.id.eq(bookDetailId),
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL_REQUEST),
                affiliation.id.eq(affiliationId)
            )
            .fetchOne()
    }

    override suspend fun findOverdueNotificationTargets(today: LocalDate): List<OverdueNotificationTarget> = dbProtection.withReadOnly {
        return@withReadOnly queryFactory
            .select(
                Projections.constructor(
                    OverdueNotificationTarget::class.java,
                    member.id,
                    bookDetail.id,
                    bookAffiliation.id,
                    book.title,
                    book.bookImage.coalesce(""),
                    bookDetail.returnDate
                )
            )
            .from(bookDetail)
            .innerJoin(bookDetail.memberEntity, member)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .where(
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL),
                bookDetail.returnDate.lt(today),
                member.deleteStatus.eq(false)
            )
            .orderBy(bookDetail.returnDate.asc(), bookDetail.id.asc())
            .fetch()
    }
}
