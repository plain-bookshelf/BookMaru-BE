package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.display.port.`in`.ViewRankingPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewRankingPageCommand
import plain.bookmaru.domain.display.port.out.RankingPagePort
import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult

private val log = KotlinLogging.logger {}

@Service
class ViewRankingPageService(
    private val rankingPagePort: RankingPagePort
): ViewRankingPageUseCase {
    override suspend fun execute(command: ViewRankingPageCommand): SliceResult<UserRankInfoResult> {
        val cacheResult = rankingPagePort.loadRankingPage(command.pageCommand, command.affiliationId)

        if (cacheResult != null) return cacheResult

        log.warn { "Ranking 캐시 miss 로 인해 빈 리스트를 반환합니다. (AffiliationId: ${command.affiliationId})" }

        return SliceResult(
            content = emptyList(),
            isLastPage = true
        )
    }
}