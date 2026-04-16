package plain.bookmaru.domain.display.service.cache

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.book.port.out.BookGenrePort
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.BookGenreResult
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult
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

        val fetchCommand = PageCommand(page = 0, size = 100)
        val sortedPopular = bookAffiliationPort.findPopularSort(fetchCommand, affiliationId)

        val popularBookSortResult = if (PlatformType.WEB == platformType) {
            popularBookSortWebResult(sortedPopular.content)
        } else {
            popularBookSortAppResult(sortedPopular.content)
        }

        mainPagePort.savePopularBooks(popularBookSortResult, platformType, affiliationId)
    }

    suspend fun upRecentBooks(platformType: PlatformType, affiliationId: Long) {
        log.info { "백그라운드 RecentBook Cache 갱신 시작 (platformType=$platformType, affiliationId=$affiliationId)" }

        val fetchCommand = PageCommand(page = 0, size = 100)
        val sortedRecent = bookAffiliationPort.findRecentSort(fetchCommand, affiliationId)

        val recentBookSortResult = if (PlatformType.WEB == platformType) {
            recentBookSortWebResult(sortedRecent.content)
        } else {
            recentBookSortAppResult(sortedRecent.content)
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

    private fun popularBookSortAppResult(content: List<BookAffiliation>): List<PopularBookSortResult> {
        return content.mapIndexed { index, ba ->
            PopularBookSortResult(
                rank = index + 1, // 통째로 가져오므로 단순히 index + 1
                id = ba.id!!,
                bookImage = ba.book.bookInfo.bookImage ?: ""
            )
        }
    }

    private fun recentBookSortAppResult(content: List<BookAffiliation>): List<RecentBookSortResult> {
        return content.map {
            RecentBookSortResult(
                id = it.id!!,
                bookImage = it.book.bookInfo.bookImage ?: ""
            )
        }
    }

    /*
    Result Web
     */

    private suspend fun popularBookSortWebResult(content: List<BookAffiliation>): List<PopularBookSortResult> {
        if (content.isEmpty()) return emptyList()

        val ids = content.map { it.book.id!! }
        val bookGenre = bookGenrePort.loadBookGenre(ids)

        return content.mapIndexed { index, ba ->
            PopularBookSortResult(
                rank = index + 1,
                id = ba.id!!,
                bookImage = ba.book.bookInfo.bookImage ?: "",
                title = ba.book.bookInfo.title,
                author = ba.book.bookInfo.author,
                genreList = bookGenre[ba.book.id!!]?.map { genre ->
                    BookGenreResult(genre = genre.genreName)
                }
            )
        }
    }

    private suspend fun recentBookSortWebResult(content: List<BookAffiliation>): List<RecentBookSortResult> {
        if (content.isEmpty()) return emptyList()

        val ids = content.map { it.book.id!! }
        val bookGenre = bookGenrePort.loadBookGenre(ids)

        return content.map {
            RecentBookSortResult(
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