package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageCommand
import plain.bookmaru.domain.display.port.out.result.ViewMyPageResult

interface ViewMyPageUseCase {
    suspend fun execute(command: ViewMyPageCommand): ViewMyPageResult
}