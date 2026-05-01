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
            log.debug { "[캐시] 애플리케이션 시작 후 워밍업을 시작합니다." }
            try {
                upAllCaches()
                log.info { "[캐시] 시작 시점 워밍업을 완료했습니다." }
            } catch (e: Exception) {
                log.error(e) { "[캐시] 시작 시점 워밍업에 실패했습니다." }
            }
        }
    }

    @Scheduled(cron = "0 0/30 * * * *")
    suspend fun upPopularBookData() {
        log.debug { "[캐시] 인기 도서 캐시를 갱신합니다." }
        val affiliationIds = affiliationPort.findAll().mapNotNull { it.id }

        coroutineScope {
            affiliationIds.flatMap { affiliationId ->
                PlatformType.entries.map { platformType ->
                    async { cacheService.upPopularBooks(platformType, affiliationId) }
                }
            }.awaitAll()
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    suspend fun upRecentBookData() {
        log.debug { "[캐시] 최신 도서 캐시를 갱신합니다." }
        val affiliationIds = affiliationPort.findAll().mapNotNull { it.id }

        coroutineScope {
            affiliationIds.flatMap { affiliationId ->
                PlatformType.entries.map { platformType ->
                    async { cacheService.upRecentBooks(platformType, affiliationId) }
                }
            }.awaitAll()
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    suspend fun upEvent() {
        log.debug { "[캐시] 이벤트 캐시를 갱신합니다." }
        val affiliationIds = affiliationPort.findAll().mapNotNull { it.id }

        coroutineScope {
            affiliationIds.map { affiliationId ->
                async { cacheService.upEvent(affiliationId) }
            }.awaitAll()
        }
    }

    private suspend fun upAllCaches() {
        coroutineScope {
            awaitAll(
                async { upPopularBookData() },
                async { upRecentBookData() },
                async { upEvent() }
            )
        }
    }
}
