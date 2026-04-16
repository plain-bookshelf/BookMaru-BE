package plain.bookmaru.domain.display.persistent.scheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.scope.CacheCoroutineScope
import plain.bookmaru.domain.display.service.cache.MainPageCacheService

private val log = KotlinLogging.logger {}

@Component
class MainPageCacheScheduler(
    private val cacheService: MainPageCacheService,
    private val affiliationPort: AffiliationPort,
    private val cacheCoroutineScope: CacheCoroutineScope
) {

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReadyEvent() {
        cacheCoroutineScope.launch {
            log.debug { "[Cache] 서버 기동 직후 캐시 적재 시도" }
            try {
                upAllCaches()
                log.info { "[Cache] 모든 캐시가 성공적으로 업데이트 완료." }
            } catch (e: Exception) {
                log.error(e) { "[Cache] 캐시 워밍업 중 오류가 발생." }
            }
        }
    }

    @Scheduled(cron = "0 0/10 * * * *")
    suspend fun upPopularBookData() {
        log.debug { "[Cache] upPopularBookData 적재 시도" }
        val affiliationList = affiliationPort.findAll()

        affiliationList
            .mapNotNull { it.id }
            .forEach {
                PlatformType.entries.forEach { platformType ->
                    cacheService.upPopularBooks(platformType, it)
                }
            }
    }

    @Scheduled(cron = "0 0 12 * * *")
    suspend fun upRecentBookData() {
        log.debug { "[Cache] upRecentBookData 적재 시도" }
        val affiliationList = affiliationPort.findAll()

        affiliationList
            .mapNotNull { it.id }
            .forEach {
                PlatformType.entries.forEach { platformType ->
                    cacheService.upRecentBooks(platformType, it)
                }
            }
    }

    @Scheduled(cron = "0 0 12 * * *")
    suspend fun upEvent() {
        log.debug { "[Cache] upEvent 적재 시도" }
        val affiliationList = affiliationPort.findAll()

        affiliationList
            .mapNotNull { it.id }
            .forEach {
                cacheService.upEvent(it)
            }
    }

    private suspend fun upAllCaches() {
        upPopularBookData()
        upRecentBookData()
        upEvent()
    }
}