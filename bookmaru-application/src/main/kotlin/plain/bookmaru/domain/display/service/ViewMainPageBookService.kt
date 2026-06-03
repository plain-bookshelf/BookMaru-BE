package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.display.port.`in`.ViewMainPagePopularBookUseCase
import plain.bookmaru.domain.display.port.`in`.ViewMainPageRecentBookUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageBookCommand
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.BookSortResult

private val log = KotlinLogging.logger {}

@Service
class ViewMainPageBookService(
    private val mainPagePort: MainPagePort
) : ViewMainPagePopularBookUseCase, ViewMainPageRecentBookUseCase {

    override suspend fun popularBookExecute(command: ViewMainPageBookCommand): List<BookSortResult> {
        val cacheResult = mainPagePort.loadPopularBooks(
            command.platformType,
            command.affiliationId
        )

        if (cacheResult != null) return cacheResult

        log.warn { "PopularBook 캐시 miss 로 인해 빈 리스트를 반환합니다. (AffiliationId: ${command.affiliationId})" }

        return emptyList()
    }

    override suspend fun recentBookExecute(command: ViewMainPageBookCommand): List<BookSortResult> {
        val cacheResult = mainPagePort.loadRecentBooks(
            command.platformType,
            command.affiliationId
        )

        if (cacheResult != null) return cacheResult

        log.warn { "RecentBook 캐시 miss 로 인해 빈 리스트를 반환합니다. (AffiliationId: ${command.affiliationId})" }

        return emptyList()
    }
}