package plain.bookmaru.domain.book.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.book.persistent.mapper.BookMapper
import plain.bookmaru.domain.book.port.out.BookPort
import plain.bookmaru.global.config.DbProtection
import kotlin.math.ceil

private const val MAX_BOOKS_SIZE = 100L

@Component
class BookPersistenceAdapter(
    private val queryFactory: JPAQueryFactory,
    private val bookMapper: BookMapper,
    private val dbProtection: DbProtection
) : BookPort {
    private val book = QBookEntity.bookEntity


    override suspend fun loadPopularSort(command: PageCommand): PageResult<Book> = dbProtection.withReadOnly {
        val offset = command.offset
        val size = command.size

        val totalPage = calculateTotalPages(size)

        if (offset >= MAX_BOOKS_SIZE) {
            return@withReadOnly PageResult(
                content = emptyList(),
                totalElements = MAX_BOOKS_SIZE,
                totalPages = totalPage,
                isLastPage = true)
        }

        val popularScore = book.rentalCount.multiply(2).add(book.likeCount)

        val limit = minOf(size.toLong(), MAX_BOOKS_SIZE - offset)

        val books =  queryFactory
            .selectFrom(book)
            .orderBy(popularScore.desc())
            .where(book.bookImage.isNotNull)
            .offset(offset)
            .limit(limit)
            .fetch()

        val (domains, totalElements) = pageResult(books)

        val isLastPage = (command.page + 1) >= totalPage

        return@withReadOnly PageResult(
            content = domains,
            totalElements = totalElements,
            totalPages = totalPage,
            isLastPage = isLastPage
        )
    }

    override suspend fun loadRecentSort(command: PageCommand): PageResult<Book> = dbProtection.withReadOnly {
        val offset = command.offset
        val size = command.size

        val totalPage = calculateTotalPages(size)

        if (offset >= MAX_BOOKS_SIZE) {
            return@withReadOnly PageResult(
                content = emptyList(),
                totalElements = MAX_BOOKS_SIZE,
                totalPages = totalPage,
                isLastPage = true)
        }

        val limit = minOf(size.toLong(), MAX_BOOKS_SIZE - offset)

        val books =  queryFactory
            .selectFrom(book)
            .orderBy(book.registrationDate.desc(), book.title.desc())
            .where(book.bookImage.isNotNull)
            .offset(offset)
            .limit(limit)
            .fetch()

        val (domains, totalElements) = pageResult(books)

        val isLastPage = (command.page + 1) >= totalPage

        return@withReadOnly PageResult(
            content = domains,
            totalElements = totalElements,
            totalPages = totalPage,
            isLastPage = isLastPage
        )
    }

    private fun calculateTotalPages(size: Int): Int = ceil(MAX_BOOKS_SIZE.toDouble() / size).toInt()

    private fun pageResult(books: List<BookEntity>): Pair<List<Book>, Long> {
        val actualTotal = queryFactory
            .select(book.count())
            .from(book)
            .where(book.bookImage.isNotNull)
            .fetchOne() ?: 0L

        val totalElements = minOf(actualTotal, MAX_BOOKS_SIZE)

        val domains = bookMapper.toDomainList(books)

        return Pair(domains, totalElements)
    }
}