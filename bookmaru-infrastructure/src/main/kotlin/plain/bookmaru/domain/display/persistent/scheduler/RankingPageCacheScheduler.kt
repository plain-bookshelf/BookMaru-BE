package plain.bookmaru.domain.display.persistent.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.display.service.cache.RankingPageCacheService

@Component
class RankingPageCacheScheduler(
    private val rankingPageCacheService: RankingPageCacheService,
    private val affiliationPort: AffiliationPort
) {

    @Scheduled(cron = "0 0 0/1 * * *")
    suspend fun upRankingData() {
        val affiliationList = affiliationPort.findAll()

        affiliationList.map {
            rankingPageCacheService.upRanking(it.id!!)
        }
    }
}