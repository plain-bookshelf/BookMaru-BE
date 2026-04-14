package plain.bookmaru.domain.search.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.index.query.QueryBuilders
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
    private val opensearchOperation: ElasticsearchOperations,
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

    private suspend fun <T> executeSearch(
        keyword: String,
        command: PageCommand,
        affiliationId: Long,
        fetcher: (List<Long>) -> List<T>,
        idSelector: (T) -> Long
    ): SliceResult<T> {
        val requestSize = command.size
        val pageable = PageRequest.of(command.page, requestSize)

        val query = searchBookAffiliation(keyword, affiliationId, pageable)

        val searchHits = opensearchOperation.search(query, BookAffiliationDocument::class.java)
        val documentIds = searchHits.searchHits.mapNotNull { it.content.id }
        val hasNext = documentIds.size > requestSize

        if (documentIds.isEmpty()) return SliceResult(emptyList(), true)

        val idToIndex = documentIds.withIndex().associate { it.value to it.index }

        val results = dbProtection.withReadOnly {
            fetcher(documentIds)
        }

        val sortedResults = results.sortedBy { idToIndex[idSelector(it)] ?: Int.MAX_VALUE }

        return sliceResult(sortedResults, hasNext)
    }

    private fun searchBookAffiliation(keyword: String, affiliationId: Long, pageable: Pageable): org.springframework.data.elasticsearch.core.query.Query {
        val boolQuery = QueryBuilders.boolQuery()
            .filter(QueryBuilders.termQuery("affiliationId", affiliationId))
            .must(
                QueryBuilders.multiMatchQuery(keyword)
                    .field("title", 5.0f)
                    .field("introduction", 1.0f)
                    .field("author", 3.0f)
                    .field("genres", 2.0f)
            )

        return NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withPageable(pageable)
            .build()
    }

    private fun <T> sliceResult(domains: List<T>, hasNext: Boolean): SliceResult<T> {
        return SliceResult(
            content = domains,
            isLastPage = !hasNext
        )
    }
}