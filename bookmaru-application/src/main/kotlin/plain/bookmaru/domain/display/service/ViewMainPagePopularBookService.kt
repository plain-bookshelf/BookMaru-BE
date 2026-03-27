package plain.bookmaru.domain.display.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.book.port.out.BookGenrePort
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.display.port.`in`.ViewMainPagePopularBookUseCase
import plain.bookmaru.domain.display.port.`in`.ViewMainPageRecentBookUseCase
import plain.bookmaru.domain.display.port.`in`.command.ViewMainPageBookCommand
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.BookGenreResult
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult
import plain.bookmaru.domain.inventory.model.BookAffiliation

private val log = KotlinLogging.logger {}

@Service
class ViewMainPagePopularBookService(
    private val bookAffiliationPort: BookAffiliationPort,
    private val bookGenrePort: BookGenrePort,
    private val mainPagePort: MainPagePort
) : ViewMainPagePopularBookUseCase, ViewMainPageRecentBookUseCase {

    override suspend fun popularBookExecute(command: ViewMainPageBookCommand): SliceResult<PopularBookSortResult> {
        val popularPageResult = popularBooksCacheCheck(
            command.pageCommand,
            command.platformType,
            command.affiliationId
        )

        return SliceResult(
            content = popularPageResult!!.content,
            isLastPage = popularPageResult.isLastPage,
        )
    }

    override suspend fun recentBookExecute(command: ViewMainPageBookCommand): SliceResult<RecentBookSortResult>? {
        val recentPageResult = recentBooksCacheCheck(
            command.pageCommand,
            command.platformType,
            command.affiliationId
        )

        return SliceResult(
            content = recentPageResult!!.content,
            isLastPage = recentPageResult.isLastPage,
        )
    }

    /*
    Cache Check
     */

    private suspend fun popularBooksCacheCheck(command: PageCommand, platformType: PlatformType, affiliationId: Long) : SliceResult<PopularBookSortResult>? {
        val startRank = command.page * command.size

        var cachePopularBooks = mainPagePort.loadPopularBooks(command, platformType, affiliationId)

        if (cachePopularBooks == null) {
            log.info { "popularBook cache 정보를 찾지 못 했습니다." }

            val sortedPopular = bookAffiliationPort.findPopularSort(command, affiliationId)
            log.info { "popularBook 정보를 불러오는데 성공했습니다." }

            val popularBookSortResult =
                if (PlatformType.WEB == platformType) { popularBookSortWebResult(startRank, sortedPopular) }
                else { popularBookSortAppResult(startRank, sortedPopular) }

            mainPagePort.savePopularBooks(popularBookSortResult, platformType, affiliationId)
            log.info { "popularBook 정보를 cache 하는데 성공했습니다." }

            val popularPageResult = SliceResult(
                content = popularBookSortResult,
                isLastPage = sortedPopular.isLastPage
            )

            cachePopularBooks = popularPageResult
        }
        return cachePopularBooks
    }

    private suspend fun recentBooksCacheCheck(command: PageCommand, platformType: PlatformType, affiliationId: Long) : SliceResult<RecentBookSortResult>? {
        var cacheRecentBooks = mainPagePort.loadRecentBooks(command, platformType, affiliationId)

        if (cacheRecentBooks == null) {
            log.info { "recentBook cache 정보를 찾지 못 했습니다." }

            val sortedRecent = bookAffiliationPort.findRecentSort(command, affiliationId)
            log.info { "recentBook 정보를 불러오는데 성공했습니다." }

            val recentBookSortResult =
                if (PlatformType.WEB == platformType) { recentBookSortWebResult(sortedRecent) }
                else { recentBookSortAppResult(sortedRecent) }

            mainPagePort.saveRecentBooks(recentBookSortResult, platformType, affiliationId)
            log.info { "recentBook 정보를 cache 하는데 성공했습니다." }

            val recentPageResult = SliceResult(
                content = recentBookSortResult,
                isLastPage = sortedRecent.isLastPage,
            )

            cacheRecentBooks = recentPageResult
        }
        return cacheRecentBooks
    }

    /*
    Result App
     */

    private suspend fun popularBookSortAppResult(startRank: Int, result: SliceResult<BookAffiliation>) : List<PopularBookSortResult> {

        val popularBookSortResult = result.content.mapIndexed { index, ba ->
            PopularBookSortResult(
                rank = startRank + index + 1, 
                id = ba.id!! , 
                bookImage = ba.book.bookInfo.bookImage
            ) 
        }

        return popularBookSortResult
    }

    private suspend fun recentBookSortAppResult(result: SliceResult<BookAffiliation>) : List<RecentBookSortResult> {

        val recentBookSortResult = result.content.map {
            RecentBookSortResult(
                id = it.id!!, 
                bookImage = it.book.bookInfo.bookImage
            ) 
        }

        return recentBookSortResult
    }

    /*
    Result Web
     */

    private suspend fun popularBookSortWebResult(startRank: Int, result: SliceResult<BookAffiliation>) : List<PopularBookSortResult> {
        val ids = result.content.map { it.book.id!! }

        val bookGenre = bookGenrePort.loadBookGenre(ids)
        log.info { "책 장르 정보를 불러오는데 성공했습니다." }

        val popularBookSortResult = result.content.mapIndexed { index, ba ->
            PopularBookSortResult(
                rank = startRank + index + 1,
                id = ba.id!!,
                bookImage = ba.book.bookInfo.bookImage,
                title = ba.book.bookInfo.title,
                author = ba.book.bookInfo.author,
                genreList = bookGenre[ba.book.id!!]?.map { genre ->
                    BookGenreResult(genre = genre.genreName)
                }
            )
        }

        return popularBookSortResult
    }

    private suspend fun recentBookSortWebResult(result: SliceResult<BookAffiliation>) : List<RecentBookSortResult> {
        val ids = result.content.map { it.book.id!! }

        val bookGenre = bookGenrePort.loadBookGenre(ids)
        log.info { "책 장르 정보를 불러오는데 성공했습니다." }

        val recentBookSortResult = result.content.map {
            RecentBookSortResult(
                id = it.id!!,
                bookImage = it.book.bookInfo.bookImage,
                title = it.book.bookInfo.title,
                author = it.book.bookInfo.author,
                genreList = bookGenre[it.book.id!!]?.map { genre ->
                    BookGenreResult(genre = genre.genreName)
                }
            )
        }

        return recentBookSortResult
    }
}