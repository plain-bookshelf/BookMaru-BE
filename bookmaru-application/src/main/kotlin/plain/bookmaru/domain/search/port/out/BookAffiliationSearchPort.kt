package plain.bookmaru.domain.search.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.search.port.out.result.AppBookAffiliationSearchResult
import plain.bookmaru.domain.search.port.out.result.WebBookAffiliationSearchResult

interface BookAffiliationSearchPort {
    suspend fun appSearchBookAffiliation(keyword: String, pageCommand: PageCommand, affiliationId: Long): SliceResult<AppBookAffiliationSearchResult>
    suspend fun webSearchBookAffiliation(keyword: String, pageCommand: PageCommand, affiliationId: Long): SliceResult<WebBookAffiliationSearchResult>
}