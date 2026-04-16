package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageLikeBookCommand
import plain.bookmaru.domain.display.port.out.result.ViewMyPageLikeBookResult

interface ViewMyPageLikeBookUseCase {
    suspend fun execute(command: ViewMyPageLikeBookCommand) : List<ViewMyPageLikeBookResult>
}