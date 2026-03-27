package plain.bookmaru.domain.inventory.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.book.vo.BookInfo
import plain.bookmaru.domain.community.persistent.entity.QBookLikeEntity
import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookAffiliationEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookDetailEntity
import plain.bookmaru.domain.inventory.persistent.mapper.BookAffiliationMapper
import plain.bookmaru.domain.inventory.persistent.repository.BookAffiliationRepository
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.inventory.port.out.result.BookDetailInfoResult
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.domain.member.persistent.entity.QMemberEntity
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
    private val member = QMemberEntity.memberEntity
    private val bookLike = QBookLikeEntity.bookLikeEntity

    override suspend fun findPopularSort(command: PageCommand, affiliationId: Long): SliceResult<BookAffiliation> = dbProtection.withReadOnly {
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

        val bookAffiliations =  queryFactory
            .selectFrom(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book).fetchJoin() // Fetch Join to solve N+1
            .innerJoin(book.affiliationEntity, affiliation).fetchJoin() // Join with Affiliation
            .orderBy(
                popularScore.desc(),
                book.title.desc()
            )
            .where(
                bookAffiliation.affiliationEntity.id.eq(affiliationId),
                book.bookImage.isNotNull)
            .offset(offset)
            .limit(limit)
            .fetch()

        log.info { "메인 페이지의 인기순 정렬 책 정보를 $offset 부터 ~ $limit 까지 가져오는데 성공했습니다." }

        return@withReadOnly sliceResult(bookAffiliations, size, offset)
    }

    override suspend fun findRecentSort(command: PageCommand, affiliationId: Long): SliceResult<BookAffiliation> = dbProtection.withReadOnly {
        val offset = command.offset
        val size = command.size

        if (offset >= MAX_BOOKS_SIZE) {
            return@withReadOnly SliceResult(
                content = emptyList(),
                isLastPage = true
            )
        }

        val limit = minOf(size.toLong(), MAX_BOOKS_SIZE - offset) + 1L

        val bookAffiliations =  queryFactory
            .selectFrom(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book).fetchJoin() // Fetch Join to solve N+1
            .innerJoin(book.affiliationEntity, affiliation).fetchJoin() // Join with Affiliation
            .orderBy(
                book.publicationDate.desc(),
                book.title.desc()
            )
            .where(
                bookAffiliation.affiliationEntity.id.eq(affiliationId),
                book.bookImage.isNotNull)
            .offset(offset)
            .limit(limit)
            .fetch()

        log.info { "메인 페이지의 최신순 정렬 책 정보를 $offset 부터 ~ $limit 까지 가져오는데 성공했습니다." }

        return@withReadOnly sliceResult(bookAffiliations, size, offset)
    }

    override suspend fun findById(id: Long): BookAffiliation? = dbProtection.withReadOnly {
        bookAffiliationRepository.findByIdOrNull(id)?.let {
            bookAffiliationMapper.toDomain(it)
        }
    }

    override suspend fun findBookInfoByBookId(bookId: Long, affiliationId: Long, memberId: Long): BookDetailInfoResult? = dbProtection.withReadOnly {
        val result = queryFactory
            .select(
                Projections.constructor(
                    BookDetailInfoResult::class.java,

                    Projections.constructor(
                        Book::class.java,
                        book.id,
                        Projections.constructor(
                            BookInfo::class.java,
                            affiliation.affiliationName,
                            book.title,
                            book.author,
                            book.publicationDate,
                            book.introduction,
                            book.bookImage,
                            book.publisher
                        )
                    ),

                    Projections.constructor(
                        BookAffiliation::class.java,
                        bookAffiliation.id,
                        Projections.constructor(
                            Book::class.java,
                            book.id,
                            Projections.constructor(
                                BookInfo::class.java,
                                affiliation.affiliationName,
                                book.title,
                                book.author,
                                book.publicationDate,
                                book.introduction,
                                book.bookImage,
                                book.publisher
                            )
                        ),
                        bookAffiliation.affiliationEntity.id,
                        bookAffiliation.rentalCount,
                        bookAffiliation.reservationCount,
                        bookAffiliation.likeCount,
                        bookAffiliation.similarityToken
                    ),

                    affiliation.affiliationName,
                    bookDetail.id.count().intValue(),
                    bookLike.id.isNotNull
                )
            )
            .from(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .innerJoin(bookAffiliation.affiliationEntity, affiliation)
            .leftJoin(bookDetail).on(
                bookDetail.bookAffiliationEntity.id.eq(bookAffiliation.id)
                    .and(bookDetail.rentalStatus.eq(RentalStatus.RETURN))
            )
            .leftJoin(bookLike).on(
                bookLike.id.bookAffiliationId.eq(bookAffiliation.id)
                    .and(bookLike.id.memberId.eq(memberId))
            )
            .where(
                book.id.eq(bookId),
                affiliation.id.eq(affiliationId)
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
                bookAffiliation.id,
                bookAffiliation.rentalCount,
                bookAffiliation.reservationCount,
                bookAffiliation.likeCount,
                bookAffiliation.similarityToken,
                bookLike.id
            )
            .fetchOne()

        return@withReadOnly result
    }

    override suspend fun update(bookAffiliation: BookAffiliation): Unit = dbProtection.withTransaction {
        val entity = bookAffiliationRepository.findByIdOrNull(bookAffiliation.id ?: 0L)
            ?: throw IllegalStateException("저장할 BookAffiliation 엔티티를 찾을 수 없습니다.")
        
        bookAffiliationMapper.updateEntity(entity, bookAffiliation)
        
        bookAffiliationRepository.save(entity)
    }

    private fun sliceResult(entities: List<BookAffiliationEntity>, requestSize: Int, offset: Long): SliceResult<BookAffiliation> {
        val hasNext = entities.size > requestSize
        val content = if (hasNext) entities.dropLast(1) else entities
        val isLastPage = !hasNext || (offset + requestSize >= MAX_BOOKS_SIZE)

        val domains = bookAffiliationMapper.toDomainList(content)

        return SliceResult(
            content = domains,
            isLastPage = isLastPage
        )
    }
}