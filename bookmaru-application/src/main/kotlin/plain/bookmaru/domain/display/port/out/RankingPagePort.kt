package plain.bookmaru.domain.display.port.out

import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult

interface RankingPagePort {
    suspend fun saveRanking(ranking: List<UserRankInfoResult>, affiliationId: Long)
    suspend fun loadRankingPage(affiliationId: Long) : List<UserRankInfoResult>?
}