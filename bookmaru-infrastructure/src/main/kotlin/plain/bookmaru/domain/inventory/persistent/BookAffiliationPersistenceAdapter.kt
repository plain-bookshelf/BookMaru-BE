package plain.bookmaru.domain.inventory.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.book.persistent.entity.QBookGenreEntity
import plain.bookmaru.domain.book.persistent.entity.QGenreEntity
import plain.bookmaru.domain.community.persistent.entity.QBookLikeEntity
import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.persistent.entity.QBookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookDetailEntity
import plain.bookmaru.domain.inventory.persistent.mapper.BookAffiliationMapper
import plain.bookmaru.domain.inventory.persistent.repository.BookAffiliationRepository
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.inventory.port.out.result.BookDetailInfoResult
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.global.config.DbProtection

private const val MAX_BOOKS_SIZE = 100L
private val log = KotlinLogging.logger {}

@Component
class BookAffiliationPersistenceAdapter(
    private val queryFactory: JPAQueryFactory,
    private val bookAffiliationRepository: BookAffiliationRepository,
    private val bookAffiliationMapper: BookAffiliationMapper,
    private val dbProtection: DbProtection
) : BookAffiliationPort {

    private val book = QBookEntity.bookEntity
    private val affiliation = QAffiliationEntity.affiliationEntity
    private val bookDetail = QBookDetailEntity.bookDetailEntity
    private val bookAffiliation = QBookAffiliationEntity.bookAffiliationEntity
    private val bookLike = QBookLikeEntity.bookLikeEntity
    private val genre = QGenreEntity.genreEntity
    private val bookGenre = QBookGenreEntity.bookGenreEntity

    /*
    find
     */

    override suspend fun findPopularSort(affiliationId: Long): List<BookAffiliation> = dbProtection.withReadOnly {
        val popularScore = bookAffiliation.rentalCount.multiply(2).add(bookAffiliation.likeCount)

        val bookAffiliations = queryFactory
            .selectFrom(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book).fetchJoin()
            .innerJoin(bookAffiliation.affiliationEntity, affiliation).fetchJoin()
            .orderBy(
                popularScore.desc(),
                book.title.desc()
            )
            .where(
                bookAffiliation.affiliationEntity.id.eq(affiliationId),
                book.bookImage.isNotNull
            )
            .limit(MAX_BOOKS_SIZE)
            .fetch()

        log.info { "\"메인 페이지의 인기순 정렬 책 정보를 가져오는데 성공했습니다." }

        return@withReadOnly bookAffiliationMapper.toDomainList(bookAffiliations)
    }

    override suspend fun findRecentSort(affiliationId: Long): List<BookAffiliation> = dbProtection.withReadOnly {
        val bookAffiliations = queryFactory
            .selectFrom(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book).fetchJoin()
            .innerJoin(bookAffiliation.affiliationEntity, affiliation).fetchJoin()
            .orderBy(
                book.publicationDate.desc(),
                book.title.desc()
            )
            .where(
                bookAffiliation.affiliationEntity.id.eq(affiliationId),
                book.bookImage.isNotNull
            )
            .limit(MAX_BOOKS_SIZE)
            .fetch()

        log.info { "메인 페이지의 최신순 정렬 책 정보를 가져오는데 성공했습니다." }

        return@withReadOnly bookAffiliationMapper.toDomainList(bookAffiliations)
    }

    override suspend fun findById(id: Long): BookAffiliation? = dbProtection.withReadOnly {
        bookAffiliationRepository.findByIdOrNull(id)?.let {
            bookAffiliationMapper.toDomain(it)
        }
    }

    override suspend fun findBookInfoByBookId(bookId: Long, affiliationId: Long, memberId: Long): BookDetailInfoResult? = dbProtection.withReadOnly {
        val bookAffiliationResult = queryFactory
            .selectFrom(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book).fetchJoin()
            .innerJoin(bookAffiliation.affiliationEntity, affiliation).fetchJoin()
            .leftJoin(book.bookGenreEntities, bookGenre).fetchJoin()
            .leftJoin(bookGenre.genreEntity, genre).fetchJoin()
            .where(
                book.id.eq(bookId),
                affiliation.id.eq(affiliationId)
            )
            .distinct()
            .fetch()
            .firstOrNull()
            ?.let(bookAffiliationMapper::toDomain)
            ?: return@withReadOnly null

        val affiliationName = queryFactory
            .select(affiliation.affiliationName)
            .from(affiliation)
            .where(affiliation.id.eq(affiliationId))
            .fetchOne() ?: ""

        val isBookLiked = queryFactory
            .selectFrom(bookLike)
            .where(
                bookLike.id.bookAffiliationId.eq(bookAffiliationResult.id),
                bookLike.id.memberId.eq(memberId)
            )
            .fetchOne() != null

        val availableCount = queryFactory
            .select(bookDetail.id.countDistinct().intValue())
            .from(bookDetail)
            .where(
                bookDetail.bookAffiliationEntity.id.eq(bookAffiliationResult.id),
                bookDetail.rentalStatus.eq(RentalStatus.RETURN)
            )
            .fetchOne() ?: 0

        return@withReadOnly BookDetailInfoResult(
            bookAffiliation = bookAffiliationResult,
            affiliationName = affiliationName,
            isBookLiked = isBookLiked,
            availableCount = availableCount
        )
    }

    /*
    update
     */

    override fun incrementLikeCount(bookAffiliationId: Long) {
        queryFactory.update(bookAffiliation)
            .set(bookAffiliation.likeCount, bookAffiliation.likeCount.add(1))
            .where(bookAffiliation.id.eq(bookAffiliationId))
            .execute()
    }

    override fun decrementLikeCount(bookAffiliationId: Long) {
        queryFactory.update(bookAffiliation)
            .set(bookAffiliation.likeCount, bookAffiliation.likeCount.add(-1))
            .where(bookAffiliation.id.eq(bookAffiliationId), bookAffiliation.likeCount.gt(0))
            .execute()
    }

    override fun decrementReservationCount(bookAffiliationId: Long) {
        queryFactory.update(bookAffiliation)
            .set(bookAffiliation.reservationCount, bookAffiliation.reservationCount.subtract(1))
            .where(bookAffiliation.id.eq(bookAffiliationId), bookAffiliation.reservationCount.gt(0))
            .execute()
    }
}