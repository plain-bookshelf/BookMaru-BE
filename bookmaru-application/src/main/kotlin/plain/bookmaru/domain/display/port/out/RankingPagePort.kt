package plain.bookmaru.domain.display.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult

interface RankingPagePort {
    suspend fun saveRanking(ranking: List<UserRankInfoResult>, affiliationId: Long)
    suspend fun loadRankingPage(command: PageCommand, affiliationId: Long) : SliceResult<UserRankInfoResult>?
}