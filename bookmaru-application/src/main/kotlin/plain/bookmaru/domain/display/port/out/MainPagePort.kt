package plain.bookmaru.domain.display.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult

interface MainPagePort {
    suspend fun savePopularBooks(books: List<PopularBookSortResult>, platformType: PlatformType, affiliationId: Long)
    suspend fun saveRecentBooks(books: List<RecentBookSortResult>, platformType: PlatformType, affiliationId: Long)
    suspend fun saveEvents(events: List<EventInfoResult>?, affiliationId: Long)

    suspend fun loadPopularBooks(command: PageCommand, platformType: PlatformType, affiliationId: Long): SliceResult<PopularBookSortResult>?
    suspend fun loadRecentBooks(command: PageCommand, platformType: PlatformType, affiliationId: Long): SliceResult<RecentBookSortResult>?
    suspend fun loadEvents(affiliationId: Long): List<EventInfoResult>?
}