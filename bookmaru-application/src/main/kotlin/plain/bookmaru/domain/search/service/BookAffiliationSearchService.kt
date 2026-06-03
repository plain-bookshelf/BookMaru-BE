package plain.bookmaru.domain.search.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.search.port.`in`.AppBookAffiliationSearchUseCase
import plain.bookmaru.domain.search.port.`in`.WebBookAffiliationSearchUseCase
import plain.bookmaru.domain.search.port.`in`.command.BookAffiliationSearchCommand
import plain.bookmaru.domain.search.port.out.BookAffiliationSearchPort
import plain.bookmaru.domain.search.port.out.result.AppBookAffiliationSearchResult
import plain.bookmaru.domain.search.port.out.result.WebBookAffiliationSearchResult

@Service
class BookAffiliationSearchService(
    private val bookAffiliationSearchPort: BookAffiliationSearchPort
): AppBookAffiliationSearchUseCase, WebBookAffiliationSearchUseCase {

    override suspend fun appExecute(command: BookAffiliationSearchCommand): SliceResult<AppBookAffiliationSearchResult> {
        val result = bookAffiliationSearchPort.appSearchBookAffiliation(
            keyword = command.keyword,
            pageCommand = command.pageCommand,
            affiliationId = command.affiliationId
        )

        return result
    }

    override suspend fun webExecute(command: BookAffiliationSearchCommand): SliceResult<WebBookAffiliationSearchResult> {
        val result = bookAffiliationSearchPort.webSearchBookAffiliation(
            keyword = command.keyword,
            pageCommand = command.pageCommand,
            affiliationId = command.affiliationId
        )

        return result
    }
}