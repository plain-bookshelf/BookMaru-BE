package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.domain.display.port.`in`.command.ViewRankingPageCommand
import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult

interface ViewRankingPageUseCase {
    suspend fun execute(command: ViewRankingPageCommand) : List<UserRankInfoResult>
}