package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.book.port.out.BookGenrePort
import plain.bookmaru.domain.book.port.out.BookPort
import plain.bookmaru.domain.display.port.`in`.AppViewMainPageUseCase
import plain.bookmaru.domain.display.port.`in`.WebViewMainPageUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageCommand
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.BookGenreResult
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult
import plain.bookmaru.domain.display.port.out.result.ViewMainPageResult
import plain.bookmaru.domain.display.vo.BookFindType
import plain.bookmaru.domain.event.port.out.EventPort

private val log = KotlinLogging.logger {}

@Service
class ViewMainPageService(
    private val bookPort: BookPort,
    private val bookGenrePort: BookGenrePort,
    private val eventPort: EventPort,
    private val mainPagePort: MainPagePort
) : AppViewMainPageUseCase, WebViewMainPageUseCase{
    override suspend fun appExecute(command: ViewMainPageCommand, platformType: PlatformType): ViewMainPageResult {
        val events = eventCacheCheck()

        return when(command.bookFindType) {
            BookFindType.POPULAR -> ViewMainPageResult(
                eventInfoResultList = events,
                popularBookSortResultList = popularBooksCacheCheck(command.pageCommand, platformType))

            BookFindType.RECENT -> ViewMainPageResult(
                eventInfoResultList = events,
                recentBookSortResultList = recentBooksCacheCheck(command.pageCommand, platformType))
        }
    }

    override suspend fun webExecute(command: PageCommand, platformType: PlatformType): ViewMainPageResult {
        val events = eventCacheCheck()
        val popularBooks = popularBooksCacheCheck(command, platformType)
        val cacheRecentBooks = recentBooksCacheCheck(command, platformType)

        return ViewMainPageResult(
            eventInfoResultList = events ?: emptyList(),
            popularBookSortResultList = popularBooks,
            recentBookSortResultList = cacheRecentBooks
        )
    }
    /*
    Cache Check
     */

    private suspend fun eventCacheCheck() : List<EventInfoResult>? {
        var cacheEvents = mainPagePort.loadEvents()

        if (cacheEvents == null) {
            log.info { "event cache 정보를 찾지 못 했습니다." }

            val eventList = eventPort.findAll()
            log.info { "event 정보를 불러오는데 성공했습니다." }

            val eventInfoResult = eventList.map {
                EventInfoResult(imageUrl = it.eventInfo.imageUrl, id = it.id!!) }

            mainPagePort.saveEvents(eventInfoResult)
            log.info { "event 정보를 cache 하는데 성공했습니다." }

            cacheEvents = eventInfoResult
        }
        return cacheEvents
    }

    private suspend fun popularBooksCacheCheck(command: PageCommand, platformType: PlatformType) : PageResult<PopularBookSortResult>? {
        val startRank = command.page * command.size

        var cachePopularBooks = mainPagePort.loadPopularBooks(command)

        if (cachePopularBooks == null) {
            log.info { "popularBook cache 정보를 찾지 못 했습니다." }

            val sortedPopular = bookPort.loadPopularSort(command)
            log.info { "popularBook 정보를 불러오는데 성공했습니다." }

            val popularBookSortResult =
                if (PlatformType.WEB == platformType) { popularBookSortWebResult(startRank, sortedPopular) }
                else { popularBookSortAppResult(startRank, sortedPopular) }

            mainPagePort.savePopularBooks(popularBookSortResult)
            log.info { "popularBook 정보를 cache 하는데 성공했습니다." }

            val popularPageResult = PageResult(
                content = popularBookSortResult,
                totalElements = sortedPopular.totalElements,
                totalPages = sortedPopular.totalPages,
                isLastPage = sortedPopular.isLastPage
            )

            cachePopularBooks = popularPageResult
        }
        return cachePopularBooks
    }

    private suspend fun recentBooksCacheCheck(command: PageCommand, platformType: PlatformType) : PageResult<RecentBookSortResult>? {
        var cacheRecentBooks = mainPagePort.loadRecentBooks(command)

        if (cacheRecentBooks == null) {
            log.info { "recentBook cache 정보를 찾지 못 했습니다." }

            val sortedRecent = bookPort.loadRecentSort(command)
            log.info { "recentBook 정보를 불러오는데 성공했습니다." }

            val recentBookSortResult =
                if (PlatformType.WEB == platformType) { recentBookSortWebResult(sortedRecent) }
                else { recentBookSortAppResult(sortedRecent) }

            mainPagePort.saveRecentBooks(recentBookSortResult)
            log.info { "recentBook 정보를 cache 하는데 성공했습니다." }

            val recentPageResult = PageResult(
                content = recentBookSortResult,
                totalElements = sortedRecent.totalElements,
                totalPages = sortedRecent.totalPages,
                isLastPage = sortedRecent.isLastPage,
            )

            cacheRecentBooks = recentPageResult
        }
        return cacheRecentBooks
    }

    /*
    Result App, Web
     */

    private suspend fun popularBookSortAppResult(startRank: Int, result: PageResult<Book>) : List<PopularBookSortResult> {

        val popularBookSortResult = result.content.mapIndexed { index, book ->
            PopularBookSortResult(rank = startRank + index + 1, id = book.id!! ,bookImage = book.bookInfo.bookImage) }

        return popularBookSortResult
    }

    private suspend fun popularBookSortWebResult(startRank: Int, result: PageResult<Book>) : List<PopularBookSortResult> {
        val ids = result.content.map { it.id!! }

        val bookGenre = bookGenrePort.loadBookGenre(ids)
        log.info { "책 장르 정보를 불러오는데 성공했습니다." }

        val popularBookSortResult = result.content.mapIndexed { index, book ->
            PopularBookSortResult(
                rank = startRank + index + 1,
                id = book.id!!,
                bookImage = book.bookInfo.bookImage,
                title = book.bookInfo.title,
                author = book.bookInfo.author,
                genreList = bookGenre[book.id]?.map { genre ->
                    BookGenreResult(genre = genre.genreName)
                }
            )
        }

        return popularBookSortResult
    }

    private suspend fun recentBookSortAppResult(result: PageResult<Book>) : List<RecentBookSortResult> {

        val recentBookSortResult = result.content.map {
            RecentBookSortResult(id = it.id!!, bookImage = it.bookInfo.bookImage) }

        return recentBookSortResult
    }

    private suspend fun recentBookSortWebResult(result: PageResult<Book>) : List<RecentBookSortResult> {
        val ids = result.content.map { it.id!! }

        val bookGenre = bookGenrePort.loadBookGenre(ids)
        log.info { "책 장르 정보를 불러오는데 성공했습니다." }

        val recentBookSortResult = result.content.map {
            RecentBookSortResult(
                id = it.id!!,
                bookImage = it.bookInfo.bookImage,
                title = it.bookInfo.title,
                author = it.bookInfo.author,
                genreList = bookGenre[it.id]?.map { genre ->
                    BookGenreResult(genre = genre.genreName)
                }
            )
        }

        return recentBookSortResult
    }
}