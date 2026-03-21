package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageBookCommand
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult

interface ViewMainPageRecentBookUseCase {
    suspend fun recentBookExecute(command: ViewMainPageBookCommand): SliceResult<RecentBookSortResult>?
}