package plain.bookmaru.domain.display.persistent.scheduler

import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.service.cache.MainPageCacheService

@Component
class MainPageCacheScheduler(
    private val cacheService: MainPageCacheService,
    private val affiliationPort: AffiliationPort
) {

    @Scheduled(cron = "0 0/10 * * * *")
    suspend fun upPopularBookData() = runBlocking {
        val affiliationList = affiliationPort.findAll()

        affiliationList
            .mapNotNull { it.id }
            .forEach {
                cacheService.upPopularBooks(PlatformType.WEB, it)
                cacheService.upPopularBooks(PlatformType.ANDROID, it)
                cacheService.upPopularBooks(PlatformType.IOS, it)
            }
    }

    @Scheduled(cron = "0 0 12 * * *")
    suspend fun upRecentBookData() = runBlocking {
        val affiliationList = affiliationPort.findAll()

        affiliationList
            .mapNotNull { it.id }
            .forEach {
                cacheService.upRecentBooks(PlatformType.WEB, it)
                cacheService.upRecentBooks(PlatformType.ANDROID, it)
                cacheService.upRecentBooks(PlatformType.IOS, it)
            }
    }

    @Scheduled(cron = "0 0 12 * * *")
    suspend fun upEvent() = runBlocking {
        val affiliationList = affiliationPort.findAll()

        affiliationList
            .mapNotNull { it.id }
            .forEach {
                cacheService.upEvent(it)
            }
    }
}