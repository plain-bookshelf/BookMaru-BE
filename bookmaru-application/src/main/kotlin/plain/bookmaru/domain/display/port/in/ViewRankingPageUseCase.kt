package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.display.port.`in`.command.ViewRankingPageCommand
import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult

interface ViewRankingPageUseCase {
    suspend fun execute(command: ViewRankingPageCommand) : SliceResult<UserRankInfoResult>
}