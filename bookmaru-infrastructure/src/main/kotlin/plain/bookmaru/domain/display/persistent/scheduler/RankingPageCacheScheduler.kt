package plain.bookmaru.domain.display.persistent.scheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.display.scope.CacheCoroutineScope
import plain.bookmaru.domain.display.service.cache.RankingPageCacheService

private val log = KotlinLogging.logger {}

@Component
class RankingPageCacheScheduler(
    private val rankingPageCacheService: RankingPageCacheService,
    private val affiliationPort: AffiliationPort,
    private val cacheCoroutineScope: CacheCoroutineScope
) {

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReadyEvent() {
        cacheCoroutineScope.launch {
            log.debug { "[캐시] 랭킹 워밍업을 시작합니다." }
            try {
                upRankingData()
                log.info { "[캐시] 랭킹 워밍업을 완료했습니다." }
            } catch (e: Exception) {
                log.error(e) { "[캐시] 랭킹 워밍업에 실패했습니다." }
            }
        }
    }

    @Scheduled(cron = "0 0 0/1 * * *")
    suspend fun upRankingData() {
        log.info { "[캐시] 랭킹 캐시를 갱신합니다." }
        val affiliationIds = affiliationPort.findAll().mapNotNull { it.id }

        coroutineScope {
            affiliationIds.map { affiliationId ->
                async { rankingPageCacheService.upRanking(affiliationId) }
            }.awaitAll()
        }
    }
}
