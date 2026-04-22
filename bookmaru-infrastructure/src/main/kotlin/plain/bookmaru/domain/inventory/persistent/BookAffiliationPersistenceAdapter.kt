package plain.bookmaru.domain.inventory.persistent

import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.book.model.BookGenre
import plain.bookmaru.domain.book.model.Genre
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.book.persistent.entity.QBookGenreEntity
import plain.bookmaru.domain.book.persistent.entity.QGenreEntity
import plain.bookmaru.domain.book.vo.BookInfo
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

        val bookAffiliations =  queryFactory
            .selectFrom(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book).fetchJoin()
            .innerJoin(bookAffiliation.affiliationEntity, affiliation).fetchJoin()
            .orderBy(
                popularScore.desc(),
                book.title.desc()
            )
            .where(
                bookAffiliation.affiliationEntity.id.eq(affiliationId),
                book.bookImage.isNotNull)
            .limit(MAX_BOOKS_SIZE)
            .fetch()

        log.info { "메인 페이지의 인기순 정렬 책 정보를 가져오는데 성공했습니다." }

        return@withReadOnly bookAffiliationMapper.toDomainList(bookAffiliations)
    }

    override suspend fun findRecentSort(affiliationId: Long): List<BookAffiliation> = dbProtection.withReadOnly {
        val bookAffiliations =  queryFactory
            .selectFrom(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book).fetchJoin() // Fetch Join to solve N+1
            .innerJoin(bookAffiliation.affiliationEntity, affiliation).fetchJoin() // Join with Affiliation
            .orderBy(
                book.publicationDate.desc(),
                book.title.desc()
            )
            .where(
                bookAffiliation.affiliationEntity.id.eq(affiliationId),
                book.bookImage.isNotNull)
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
        val results = queryFactory
            .from(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .leftJoin(book.bookGenreEntities, bookGenre)
            .leftJoin(bookGenre.genreEntity, genre)
            .leftJoin(bookLike).on(
                bookLike.id.bookAffiliationId.eq(bookAffiliation.id)
                    .and(bookLike.id.memberId.eq(memberId))
            )
            .where(
                book.id.eq(bookId),
                affiliation.id.eq(affiliationId)
            )
            .transform(
                groupBy(bookAffiliation.id).list(
                    Projections.constructor(
                        BookDetailInfoResult::class.java,

                        bookAffiliationProjection(),

                        affiliation.affiliationName,
                        ExpressionUtils.`as`(
                            JPAExpressions.select(bookDetail.id.countDistinct())
                                .from(bookDetail)
                                .where(
                                    bookDetail.bookAffiliationEntity.id.eq(bookAffiliation.id),
                                    bookDetail.rentalStatus.eq(RentalStatus.RETURN)
                                ),
                            "availableCount"
                        ),
                        bookLike.id.isNotNull
                    )
                )
            )

        return@withReadOnly results.firstOrNull()
    }

    override suspend fun findAllWithBookAndGenresAndAffiliation(): List<BookAffiliation> = dbProtection.withReadOnly {
        val bookAffiliation = queryFactory
            .from(bookAffiliation)
            .distinct()
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .leftJoin(book.bookGenreEntities, bookGenre)
            .leftJoin(bookGenre.genreEntity, genre)
            .transform(
                groupBy(bookAffiliation.id).list(
                    bookAffiliationProjection()
                )
            )

        return@withReadOnly bookAffiliation
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

    /*
    private helper method
     */

    /*
     * Genre -> BookGenre Projection
     */
    private fun bookGenreListProjection() = list(
        Projections.constructor(
            BookGenre::class.java,
            bookGenre.id,
            Projections.constructor(
                Genre::class.java,
                genre.id,
                genre.genreName
            ).skipNulls()
        ).skipNulls()
    )

    /**
     * BookInfo -> Book Projection (장르 리스트 포함)
     */
    private fun bookProjection() = Projections.constructor(
        Book::class.java,
        book.id,
        Projections.constructor(
            BookInfo::class.java,
            book.title,
            book.author,
            book.publicationDate,
            book.introduction,
            book.bookImage,
            book.publisher
        ),
        bookGenreListProjection()
    )

    /**
     * BookAffiliation Projection
     */
    private fun bookAffiliationProjection() = Projections.constructor(
        BookAffiliation::class.java,
        bookAffiliation.id,
        bookProjection(),
        affiliation.id,
        bookAffiliation.rentalCount,
        bookAffiliation.reservationCount,
        bookAffiliation.likeCount,
        bookAffiliation.similarityToken
    )
}