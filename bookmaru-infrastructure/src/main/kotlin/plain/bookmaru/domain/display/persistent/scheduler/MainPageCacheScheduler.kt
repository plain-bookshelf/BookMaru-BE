package plain.bookmaru.domain.display.persistent.scheduler

import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.service.MainPageCacheService

@Component
class MainPageCacheScheduler(
    private val cacheService: MainPageCacheService,
    private val affiliationPort: AffiliationPort
) {

    @Scheduled(cron = "0 0/10 * * * *")
    suspend fun upPopularBookData() = runBlocking {
        val affiliationList = affiliationPort.findAll()

        affiliationList.map {
            cacheService.upPopularBooks(PlatformType.WEB, it.id!!)
            cacheService.upPopularBooks(PlatformType.ANDROID, it.id!!)
            cacheService.upPopularBooks(PlatformType.IOS, it.id!!)
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    suspend fun upRecentBookData() = runBlocking {
        val affiliationList = affiliationPort.findAll()

        affiliationList.map {
            cacheService.upRecentBooks(PlatformType.WEB, it.id!!)
            cacheService.upRecentBooks(PlatformType.ANDROID, it.id!!)
            cacheService.upRecentBooks(PlatformType.IOS, it.id!!)
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    suspend fun upEvent() = runBlocking {
        val affiliationList = affiliationPort.findAll()

        affiliationList.map {
            cacheService.upEvent(it.id!!)
        }
    }
}