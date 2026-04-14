package plain.bookmaru.domain.search.persistent.repository

import org.springframework.data.repository.CrudRepository
import plain.bookmaru.domain.search.persistent.document.BookAffiliationDocument

interface BookAffiliationSearchRepository: CrudRepository<BookAffiliationDocument, Long> {
}