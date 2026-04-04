package plain.bookmaru.domain.search.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.book.persistent.entity.QBookEntity
import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.persistent.entity.QBookAffiliationEntity
import plain.bookmaru.domain.search.persistent.document.BookAffiliationDocument
import plain.bookmaru.domain.search.persistent.repository.BookAffiliationSearchRepository
import plain.bookmaru.domain.search.port.out.BookAffiliationSearchPort
import plain.bookmaru.domain.search.port.out.result.AppBookAffiliationSearchResult
import plain.bookmaru.domain.search.port.out.result.WebBookAffiliationSearchResult
import plain.bookmaru.global.config.DbProtection

@Component

class BookAffiliationSearchPersistenceAdapter(
    private val bookAffiliationSearchRepository: BookAffiliationSearchRepository,
    private val elasticsearchOperations: ElasticsearchOperations,
    private val queryFactory: JPAQueryFactory,
    private val dbProtection: DbProtection
): BookAffiliationSearchPort {
    private val bookAffiliation = QBookAffiliationEntity.bookAffiliationEntity
    private val book = QBookEntity.bookEntity

    override suspend fun saveAll(bookAffiliations: List<BookAffiliation>): Unit = dbProtection.withTransaction {
        val documents = bookAffiliations.map { BookAffiliationDocument.toDocument(it) }
        bookAffiliationSearchRepository.saveAll(documents)
    }

    override suspend fun appSearchBookAffiliation(
        keyword: String,
        pageCommand: PageCommand,
        affiliationId: Long
    ): SliceResult<AppBookAffiliationSearchResult> = executeSearch(keyword, pageCommand, affiliationId,
        fetcher = {
            queryFactory
                .select(
                    Projections.constructor(
                        AppBookAffiliationSearchResult::class.java,
                        bookAffiliation.id,
                        book.bookImage)
                )
                .from(bookAffiliation)
                .innerJoin(bookAffiliation.bookEntity, book)
                .where(bookAffiliation.id.`in`(it))
                .fetch()
        },
        idSelector = { it.bookAffiliationId }
    )

    override suspend fun webSearchBookAffiliation(
        keyword: String,
        pageCommand: PageCommand,
        affiliationId: Long
    ): SliceResult<WebBookAffiliationSearchResult> = executeSearch(keyword, pageCommand, affiliationId,
        fetcher = {
            queryFactory
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
                .where(bookAffiliation.id.`in`(it))
                .fetch()
        },
        idSelector = { it.bookAffiliationId }
    )

    /*
    private helper method
     */

    private suspend fun <T> executeSearch(
        keyword: String,
        command: PageCommand,
        affiliationId: Long,
        fetcher: (List<Long>) -> List<T>,
        idSelector: (T) -> Long
    ): SliceResult<T> {
        val pageable = PageRequest.of(command.page, command.size + 1)
        val query = searchBookAffiliation(keyword, affiliationId, pageable)

        val searchHits = elasticsearchOperations.search(query, BookAffiliationDocument::class.java)
        val documentIds = searchHits.searchHits.mapNotNull { it.content.id }

        if (documentIds.isEmpty()) return SliceResult(emptyList(), true)

        val idToIndex = documentIds.withIndex().associate { it.value to it.index }


        val results = dbProtection.withReadOnly {
            fetcher(documentIds)
        }

        val sortedResults = results.sortedBy { idToIndex[idSelector(it)] ?: Int.MAX_VALUE }

        return sliceResult(sortedResults, command.size)
    }

    private fun searchBookAffiliation(keyword: String, affiliationId: Long, pageable: Pageable): NativeQuery {
        return NativeQuery.builder()
            .withQuery { q ->
                q.bool { b ->
                    b.filter { f ->
                        f.term { t -> t.field("affiliationId").value(affiliationId) }
                    }
                    b.must { m ->
                        m.multiMatch { mm ->
                            mm.query(keyword)
                                .fields(
                                    "title^5.0",
                                    "introduction^1.0",
                                    "author^3.0",
                                    "genres^2.0"
                                )
                        }
                    }
                }
            }
            .withPageable(pageable)
            .build()
    }

    private fun <T> sliceResult(domains: List<T>, requestSize: Int): SliceResult<T> {
        val hasNext = domains.size > requestSize
        val contents = if (hasNext) domains.dropLast(1) else domains

        return SliceResult(
            content = contents,
            isLastPage = !hasNext,
        )
    }
}