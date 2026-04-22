package plain.bookmaru.domain.search.persistent

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.inventory.persistent.entity.QBookAffiliationEntity
import plain.bookmaru.domain.search.port.out.BookAffiliationSearchPort
import plain.bookmaru.domain.search.port.out.result.AppBookAffiliationSearchResult
import plain.bookmaru.domain.search.port.out.result.WebBookAffiliationSearchResult
import plain.bookmaru.global.config.DbProtection

@Component
class BookAffiliationSearchPersistenceAdapter(
    private val queryFactory: JPAQueryFactory,
    private val dbProtection: DbProtection
): BookAffiliationSearchPort {
    private val bookAffiliation = QBookAffiliationEntity.bookAffiliationEntity
    private val book = QBookEntity.bookEntity

    override suspend fun appSearchBookAffiliation(
        keyword: String,
        pageCommand: PageCommand,
        affiliationId: Long
    ): SliceResult<AppBookAffiliationSearchResult> = dbProtection.withReadOnly {
        val normalizedKeyword = keyword.trim()
        if (normalizedKeyword.isEmpty()) {
            return@withReadOnly SliceResult(emptyList(), true)
        }

        val rank = rankExpression(normalizedKeyword)
        val results = queryFactory
            .select(
                Projections.constructor(
                    AppBookAffiliationSearchResult::class.java,
                    bookAffiliation.id,
                    book.bookImage
                )
            )
            .from(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .where(
                affiliationPredicate(affiliationId),
                matchesKeyword(normalizedKeyword)
            )
            .orderBy(
                rank.desc(),
                bookAffiliation.id.desc()
            )
            .offset(pageCommand.offset)
            .limit((pageCommand.size + 1).toLong())
            .fetch()

        return@withReadOnly sliceResult(results, pageCommand.size)
    }

    override suspend fun webSearchBookAffiliation(
        keyword: String,
        pageCommand: PageCommand,
        affiliationId: Long
    ): SliceResult<WebBookAffiliationSearchResult> = dbProtection.withReadOnly {
        val normalizedKeyword = keyword.trim()
        if (normalizedKeyword.isEmpty()) {
            return@withReadOnly SliceResult(emptyList(), true)
        }

        val rank = rankExpression(normalizedKeyword)
        val results = queryFactory
            .select(
                Projections.constructor(
                    WebBookAffiliationSearchResult::class.java,
                    bookAffiliation.id,
                    book.title,
                    book.author,
                    book.introduction,
                    book.publisher,
                    book.publicationDate,
                    book.bookImage
                )
            )
            .from(bookAffiliation)
            .innerJoin(bookAffiliation.bookEntity, book)
            .where(
                affiliationPredicate(affiliationId),
                matchesKeyword(normalizedKeyword)
            )
            .orderBy(
                rank.desc(),
                bookAffiliation.id.desc()
            )
            .offset(pageCommand.offset)
            .limit((pageCommand.size + 1).toLong())
            .fetch()

        return@withReadOnly sliceResult(results, pageCommand.size)
    }

    private fun toPrefixQuery(keyword: String): String {
        return keyword.trim()
            .split("\\s+".toRegex()) // 공백으로 단어 분리
            .filter { it.isNotBlank() }
            .joinToString(" & ") { "$it:*" } // 각 단어 뒤에 :* 추가
    }

    private fun rankExpression(keyword: String): NumberExpression<Double> {
        return Expressions.numberTemplate(
            Double::class.javaObjectType,
            "function('ts_rank', {0}, function('to_tsquery', 'simple', {1}))",
            bookAffiliation.similarityToken,
            toPrefixQuery(keyword)
        )
    }

    private fun matchesKeyword(keyword: String): BooleanExpression {
        return Expressions.numberTemplate(
            Double::class.javaObjectType,
            "function('ts_rank', {0}, function('to_tsquery', 'simple', {1}))",
            bookAffiliation.similarityToken,
            toPrefixQuery(keyword)
        ).gt(0.0)
    }

    private fun affiliationPredicate(affiliationId: Long): BooleanExpression {
        return bookAffiliation.affiliationEntity.id.eq(affiliationId)
    }


    private fun <T> sliceResult(results: List<T>, requestSize: Int): SliceResult<T> {
        val hasNext = results.size > requestSize
        val content = if (hasNext) results.dropLast(1) else results

        return SliceResult(
            content = content,
            isLastPage = !hasNext
        )
    }
}