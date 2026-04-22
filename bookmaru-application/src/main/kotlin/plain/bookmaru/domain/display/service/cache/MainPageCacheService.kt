package plain.bookmaru.domain.display.service.cache

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.book.port.out.BookGenrePort
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.BookGenreResult
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.BookSortResult
import plain.bookmaru.domain.event.port.out.EventPort
import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort

private val log = KotlinLogging.logger {}

@Service
class MainPageCacheService(
    private val mainPagePort: MainPagePort,
    private val eventPort: EventPort,
    private val bookGenrePort: BookGenrePort,
    private val bookAffiliationPort: BookAffiliationPort
) {
    suspend fun upPopularBooks(platformType: PlatformType, affiliationId: Long) {
        log.info { "백그라운드 PopularBook Cache 갱신 시작 (platformType=$platformType, affiliationId=$affiliationId)" }

        val sortedPopular = bookAffiliationPort.findPopularSort(affiliationId)

        val popularBookSortResult = if (PlatformType.WEB == platformType) {
            bookSortWebResult(sortedPopular)
        } else {
            popularBookSortAppResult(sortedPopular)
        }

        mainPagePort.savePopularBooks(popularBookSortResult, platformType, affiliationId)
    }

    suspend fun upRecentBooks(platformType: PlatformType, affiliationId: Long) {
        log.info { "백그라운드 RecentBook Cache 갱신 시작 (platformType=$platformType, affiliationId=$affiliationId)" }

        val sortedRecent = bookAffiliationPort.findRecentSort(affiliationId)

        val recentBookSortResult = if (PlatformType.WEB == platformType) {
            bookSortWebResult(sortedRecent)
        } else {
            recentBookSortAppResult(sortedRecent)
        }

        mainPagePort.saveRecentBooks(recentBookSortResult, platformType, affiliationId)
    }

    suspend fun upEvent(affiliationId: Long) {
        log.info { "백그라운드 Event Cache 갱신 시작 (affiliationId=$affiliationId)" }

        val events = eventPort.findAll()

        val eventInfos = events?.map {
            EventInfoResult(
                imageUrl = it.eventInfo.imageUrl,
                id = it.id!!
            )
        }

        mainPagePort.saveEvents(eventInfos, affiliationId)
    }

    /*
    Result App
     */

    private fun popularBookSortAppResult(content: List<BookAffiliation>): List<BookSortResult> {
        return content.map {
            BookSortResult(
                id = it.id!!,
                bookImage = it.book.bookInfo.bookImage ?: ""
            )
        }
    }

    private fun recentBookSortAppResult(content: List<BookAffiliation>): List<BookSortResult> {
        return content.map {
            BookSortResult(
                id = it.id!!,
                bookImage = it.book.bookInfo.bookImage ?: ""
            )
        }
    }

    /*
    Result Web
     */

    private suspend fun bookSortWebResult(content: List<BookAffiliation>): List<BookSortResult> {
        if (content.isEmpty()) return emptyList()

        val ids = content.map { it.book.id!! }
        val bookGenre = bookGenrePort.loadBookGenre(ids)

        return content.map {
            BookSortResult(
                id = it.id!!,
                bookImage = it.book.bookInfo.bookImage ?: "",
                title = it.book.bookInfo.title,
                author = it.book.bookInfo.author,
                genreList = bookGenre[it.book.id!!]?.map { genre ->
                    BookGenreResult(genre = genre.genreName)
                }
            )
        }
    }
}