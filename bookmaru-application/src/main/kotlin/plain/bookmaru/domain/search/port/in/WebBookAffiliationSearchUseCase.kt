package plain.bookmaru.domain.search.port.`in`

import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.search.port.`in`.command.BookAffiliationSearchCommand
import plain.bookmaru.domain.search.port.out.result.WebBookAffiliationSearchResult

interface WebBookAffiliationSearchUseCase {
    suspend fun webExecute(command: BookAffiliationSearchCommand): SliceResult<WebBookAffiliationSearchResult>
}