package plain.bookmaru.domain.lending.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.inventory.model.BookDetail
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
import plain.bookmaru.domain.lending.port.out.result.RentalRequestCheckResult
import plain.bookmaru.domain.member.persistent.entity.QMemberEntity
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.global.config.DbProtection
import java.time.LocalDate

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

    override suspend fun update(domain: BookDetail) = dbProtection.withTransaction {
        val now = LocalDate.now()

        val recordEntity = queryFactory
            .selectFrom(bookRentalRecord)
            .join(bookRentalRecord.bookDetail, bookDetail).fetchJoin()
            .where(
                bookRentalRecord.bookDetail.id.eq(domain.id),
                bookRentalRecord.returnDate.isNull
            )
            .fetchOne() ?: throw NotFoundRentalRecordException("해당 책의 진행 중인 대여 기록을 찾지 못했습니다.")

        recordEntity.returnDate = now

        val detailEntity = recordEntity.bookDetail
        detailEntity.rentalStatus = RentalStatus.RETURN
        detailEntity.memberEntity = null
        detailEntity.returnDate = null
    }

    override suspend fun findRentalRequestBookByAffiliationId(affiliationId: Long): List<RentalRequestCheckResult> = dbProtection.withReadOnly {
        return@withReadOnly queryFactory
            .select(
                Projections.constructor(
                    RentalRequestCheckResult::class.java,
                    member.id,
                    member.nickname,
                    book.title,
                    bookDetail.callNumber
                )
            )
            .from(bookRentalRecord)
            .innerJoin(bookRentalRecord.bookDetail, bookDetail)
            .innerJoin(bookDetail.bookAffiliationEntity, bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .innerJoin(bookRentalRecord.member, member)
            .where(
                bookDetail.rentalStatus.eq(RentalStatus.RENTAL_REQUEST),
                affiliation.id.eq(affiliationId)
            )
            .orderBy(
                book.id.desc()
            )
            .fetch()
    }
}
