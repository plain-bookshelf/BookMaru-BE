package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageEventCommand
import plain.bookmaru.domain.display.port.out.result.ViewMainPageEventResult

interface ViewMainPageEventUseCase {
    suspend fun execute(command: ViewMainPageEventCommand) : ViewMainPageEventResult
}