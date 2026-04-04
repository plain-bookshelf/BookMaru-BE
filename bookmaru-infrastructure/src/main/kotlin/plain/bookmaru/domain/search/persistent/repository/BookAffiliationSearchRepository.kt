package plain.bookmaru.domain.search.persistent.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import plain.bookmaru.domain.search.persistent.document.BookAffiliationDocument

interface BookAffiliationSearchRepository: ElasticsearchRepository<BookAffiliationDocument, Long> {
}