package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageBookCommand
import plain.bookmaru.domain.display.port.out.result.BookSortResult

interface ViewMainPagePopularBookUseCase {
    suspend fun popularBookExecute(command: ViewMainPageBookCommand): List<BookSortResult>
}