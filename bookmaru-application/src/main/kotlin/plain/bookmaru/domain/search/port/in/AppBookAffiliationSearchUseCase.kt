package plain.bookmaru.domain.search.port.`in`

import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.search.port.`in`.command.BookAffiliationSearchCommand
import plain.bookmaru.domain.search.port.out.result.AppBookAffiliationSearchResult

interface AppBookAffiliationSearchUseCase {
    suspend fun appExecute(command: BookAffiliationSearchCommand): SliceResult<AppBookAffiliationSearchResult>
}