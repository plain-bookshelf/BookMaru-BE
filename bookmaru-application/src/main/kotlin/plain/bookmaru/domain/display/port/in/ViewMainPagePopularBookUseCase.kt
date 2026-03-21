package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageBookCommand
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult

interface ViewMainPagePopularBookUseCase {
    suspend fun popularBookExecute(command: ViewMainPageBookCommand): SliceResult<PopularBookSortResult>
}