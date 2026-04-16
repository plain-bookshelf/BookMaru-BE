package plain.bookmaru.domain.display.persistent.scheduler

import io.github.oshai.kotlinlogging.KotlinLogging
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
            log.debug { "[Cache] 서버 기동 직후 캐시 적재 시도" }
            try {
                upRankingData()
                log.info { "[Cache] 모든 캐시가 성공적으로 업데이트 완료." }
            } catch (e: Exception) {
                log.error(e) { "[Cache] 캐시 워밍업 중 오류가 발생." }
            }
        }
    }

    @Scheduled(cron = "0 0 0/1 * * *")
    suspend fun upRankingData() {
        log.info { "[Cache] ranking 적재 시도" }
        val affiliationList = affiliationPort.findAll()

        affiliationList
            .mapNotNull { it.id }
            .forEach {
                rankingPageCacheService.upRanking(it)
            }
    }
}