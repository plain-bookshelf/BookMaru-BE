package plain.bookmaru.domain.display.port.out

import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.BookSortResult

interface MainPagePort {
    suspend fun savePopularBooks(books: List<BookSortResult>, platformType: PlatformType, affiliationId: Long)
    suspend fun saveRecentBooks(books: List<BookSortResult>, platformType: PlatformType, affiliationId: Long)
    suspend fun saveEvents(events: List<EventInfoResult>?, affiliationId: Long)

    suspend fun loadPopularBooks(platformType: PlatformType, affiliationId: Long): List<BookSortResult>?
    suspend fun loadRecentBooks(platformType: PlatformType, affiliationId: Long): List<BookSortResult>?
    suspend fun loadEvents(affiliationId: Long): List<EventInfoResult>?
}