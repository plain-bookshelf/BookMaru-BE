package plain.bookmaru.domain.display.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult

interface MainPagePort {
    suspend fun savePopularBooks(books: List<PopularBookSortResult>)
    suspend fun saveRecentBooks(books: List<RecentBookSortResult>)
    suspend fun saveEvents(events: List<EventInfoResult>)

    suspend fun loadPopularBooks(command: PageCommand): PageResult<PopularBookSortResult>?
    suspend fun loadRecentBooks(command: PageCommand): PageResult<RecentBookSortResult>?
    suspend fun loadEvents(): List<EventInfoResult>?
}