package plain.bookmaru.domain.event.port.`in`

import plain.bookmaru.domain.event.port.`in`.command.ViewEventDetailPageCommand
import plain.bookmaru.domain.event.port.out.result.ViewEventDetailPageResult

interface ViewEventDetailPageUseCase {
    suspend fun execute(command: ViewEventDetailPageCommand) : ViewEventDetailPageResult
}