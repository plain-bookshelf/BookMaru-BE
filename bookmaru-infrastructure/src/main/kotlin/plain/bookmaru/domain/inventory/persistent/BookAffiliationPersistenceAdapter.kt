package plain.bookmaru.domain.inventory.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.book.persistent.mapper.BookMapper
import plain.bookmaru.domain.book.persistent.repository.BookRepository
import plain.bookmaru.domain.book.vo.BookInfo
import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.persistent.entity.QBookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookDetailEntity
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.inventory.port.out.result.BookDetailInfoResult
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.global.config.DbProtection

private const val MAX_BOOKS_SIZE = 100L
private val log = KotlinLogging.logger {}

@Component
class BookAffiliationPersistenceAdapter(
    private val queryFactory: JPAQueryFactory,
    private val bookRepository: BookRepository,
    private val bookMapper: BookMapper,
    private val dbProtection: DbProtection
) : BookAffiliationPort {

    private val book = QBookEntity.bookEntity
    private val affiliation = QAffiliationEntity.affiliationEntity
    private val bookDetail = QBookDetailEntity.bookDetailEntity
    private val bookAffiliation = QBookAffiliationEntity.bookAffiliationEntity

    override suspend fun loadPopularSort(command: PageCommand, affiliationId: Long): SliceResult<Book> = dbProtection.withReadOnly {
        val offset = command.offset
        val size = command.size

        if (offset >= MAX_BOOKS_SIZE) {
            return@withReadOnly SliceResult(
                content = emptyList(),
                isLastPage = true
            )
        }

        val popularScore = bookAffiliation.rentalCount.multiply(2).add(bookAffiliation.likeCount)

        val limit = minOf(size.toLong(), MAX_BOOKS_SIZE - offset) + 1L

        val books =  queryFactory
            .select(bookAffiliation.bookEntity)
            .from(bookAffiliation)
            .orderBy(
                popularScore.desc(),
                bookAffiliation.bookEntity.title.desc()
            )
            .where(
                bookAffiliation.affiliationEntity.id.eq(affiliationId),
                book.bookImage.isNotNull)
            .offset(offset)
            .limit(limit)
            .fetch()

        log.info { "메인 페이지의 인기순 정렬 책 정보를 $offset 부터 ~ $limit 까지 가져오는데 성공했습니다." }

        return@withReadOnly sliceResult(books, size, offset)
    }

    override suspend fun loadRecentSort(command: PageCommand, affiliationId: Long): SliceResult<Book> = dbProtection.withReadOnly {
        val offset = command.offset
        val size = command.size

        if (offset >= MAX_BOOKS_SIZE) {
            return@withReadOnly SliceResult(
                content = emptyList(),
                isLastPage = true
            )
        }

        val limit = minOf(size.toLong(), MAX_BOOKS_SIZE - offset) + 1L

        val books =  queryFactory
            .select(bookAffiliation.bookEntity)
            .from(bookAffiliation)
            .orderBy(
                bookAffiliation.bookEntity.publicationDate.desc(),
                bookAffiliation.bookEntity.title.desc()
            )
            .where(
                bookAffiliation.affiliationEntity.id.eq(affiliationId),
                bookAffiliation.bookEntity.bookImage.isNotNull)
            .offset(offset)
            .limit(limit)
            .fetch()

        log.info { "메인 페이지의 최신순 정렬 책 정보를 $offset 부터 ~ $limit 까지 가져오는데 성공했습니다." }

        return@withReadOnly sliceResult(books, size, offset)
    }

    override suspend fun findById(id: Long): Book? = dbProtection.withReadOnly {
        bookRepository.findBookEntityById(id)?.let {
            bookMapper.toDomain(it)
        }
    }

    override suspend fun findBookInfoByBookId(bookId: Long, affiliationId: Long): BookDetailInfoResult? = dbProtection.withReadOnly {
        val bookInfo = queryFactory
            .select(
                Projections.constructor(
                    BookDetailInfoResult::class.java,

                    Projections.constructor(
                        Book::class.java,
                        book.id,

                        Projections.constructor(
                            BookInfo::class.java,
                            affiliation.affiliationName,
                            bookAffiliation.bookEntity.title,
                            bookAffiliation.bookEntity.author,
                            bookAffiliation.bookEntity.publicationDate,
                            bookAffiliation.bookEntity.introduction,
                            bookAffiliation.bookEntity.bookImage,
                            bookAffiliation.bookEntity.publisher
                        ),
                    ),

                    Projections.constructor(
                        BookAffiliation::class.java,
                        bookAffiliation.id,
                        bookAffiliation.bookEntity.id,
                        bookAffiliation.affiliationEntity.id,
                        bookAffiliation.rentalCount,
                        bookAffiliation.reservationCount,
                        bookAffiliation.likeCount
                    ),

                    affiliation.affiliationName,
                    bookDetail.id.count().intValue()
                )
            )
            .from(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .leftJoin(bookDetail).on(
                bookDetail.bookAffiliationEntity.id.eq(bookAffiliation.id)
                    .and(bookDetail.rentalStatus.eq(RentalStatus.RETURN))
            )
            .where(
                bookAffiliation.bookEntity.id.eq(bookId),
                bookAffiliation.affiliationEntity.id.eq(affiliationId)
            )
            .groupBy(
                book.id,
                affiliation.id,
                affiliation.affiliationName,
                book.title,
                book.author,
                book.publicationDate,
                book.introduction,
                book.bookImage,
                book.publisher,
                bookAffiliation.rentalCount,
                bookAffiliation.reservationCount,
                bookAffiliation.likeCount
            )
            .fetchOne()

        log.info { "${bookAffiliation.bookEntity.title} 책 제목의 책 상세 페이지의 정보를 가져오는데 성공했습니다." }

        return@withReadOnly bookInfo
    }

    /*
    private helper method
     */

    private fun sliceResult(books: List<BookEntity>, requestSize: Int, offset: Long): SliceResult<Book> {
        val hasNext = books.size > requestSize

        val content = if (hasNext) books.dropLast(1) else books

        val isLastPage = !hasNext || (offset + requestSize >= MAX_BOOKS_SIZE)

        val domains = bookMapper.toDomainList(content)

        return SliceResult(
            content = domains,
            isLastPage = isLastPage
        )
    }
}