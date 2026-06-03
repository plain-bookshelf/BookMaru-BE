package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.domain.display.port.`in`.command.ViewMyPageLendingInfoCommand
import plain.bookmaru.domain.display.port.out.result.LendingBookListResult

interface ViewMyPageLendingInfoUseCase {
    suspend fun execute(command: ViewMyPageLendingInfoCommand): LendingBookListResult
}