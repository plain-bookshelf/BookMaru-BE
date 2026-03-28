package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.domain.display.port.`in`.command.ViewBookDetailPageCommand
import plain.bookmaru.domain.display.port.out.result.BookDetailPageResult

interface ViewBookDetailPageUseCase {
    suspend fun execute(command: ViewBookDetailPageCommand) : BookDetailPageResult
}