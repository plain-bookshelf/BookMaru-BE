package plain.bookmaru.domain.display.service.cache

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.display.port.out.RankingPagePort
import plain.bookmaru.domain.member.port.out.MemberPort

private val log = KotlinLogging.logger {}

@Service
class RankingPageCacheService(
    private val rankingPagePort: RankingPagePort,
    private val memberPort: MemberPort
) {

    suspend fun upRanking(affiliationId: Long) {
        log.info { "백그라운드 Ranking Cache 갱신 시작 (affiliationId=$affiliationId)" }
        val ranking = memberPort.findUserRanking(affiliationId)

        rankingPagePort.saveRanking(ranking, affiliationId)
    }
}