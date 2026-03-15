package plain.bookmaru.domain.display.persistent

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult
import plain.bookmaru.global.config.DbProtection
import java.time.Duration
import kotlin.math.ceil

private const val EVENT_KEY = "cache:display:main:event"
private const val POPULAR_BOOK_KEY = "cache:display:main:popular"
private const val RECENT_BOOK_KEY = "cache:display:main:recent"

private val EVENT_TTL = Duration.ofHours(24)
private val POPULAR_BOOK_TTL = Duration.ofMinutes(10)
private val RECENT_BOOK_TTL = Duration.ofHours(24)

@Component
class MainPagePersistenceAdapter(
    @Qualifier("cacheRedisTemplate")
    private val cacheRedisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
    private val dbProtection: DbProtection
) : MainPagePort {
    /*
    SAVE
     */
    override suspend fun savePopularBooks(books: List<PopularBookSortResult>) = dbProtection.withTransaction {
        if (books.isEmpty()) return@withTransaction

        val jsonList = books.map { objectMapper.writeValueAsString(it) }

        cacheRedisTemplate.opsForList().rightPushAll(POPULAR_BOOK_KEY, jsonList)
        cacheRedisTemplate.expire(POPULAR_BOOK_KEY, POPULAR_BOOK_TTL)
    }

    override suspend fun saveRecentBooks(books: List<RecentBookSortResult>) = dbProtection.withTransaction {
        if (books.isEmpty()) return@withTransaction

        val jsonList = books.map { objectMapper.writeValueAsString(it) }

        cacheRedisTemplate.opsForList().rightPushAll(RECENT_BOOK_KEY, jsonList)
        cacheRedisTemplate.expire(RECENT_BOOK_KEY, RECENT_BOOK_TTL)
    }

    override suspend fun saveEvents(events: List<EventInfoResult>) = dbProtection.withTransaction {
        if (events.isEmpty()) return@withTransaction

        val jsonList = events.map { objectMapper.writeValueAsString(it) }

        cacheRedisTemplate.opsForList().rightPushAll(EVENT_KEY, jsonList)
        cacheRedisTemplate.expire(EVENT_KEY, EVENT_TTL)
    }
    /*
    LOAD
     */

    override suspend fun loadPopularBooks(command: PageCommand): PageResult<PopularBookSortResult>? = dbProtection.withReadOnly {
        val size = command.size
        val start = command.offset
        val end = start + size - 1

        val jsonList = cacheRedisTemplate.opsForList().range(POPULAR_BOOK_KEY, start, end)

        if (jsonList.isNullOrEmpty()) return@withReadOnly null

        val books = jsonList.map { objectMapper.readValue(it as String, PopularBookSortResult::class.java) }

        val totalElements = cacheRedisTemplate.opsForList().size(RECENT_BOOK_KEY) ?: 0L
        val totalPages = ceil(totalElements.toDouble() / size).toInt()
        val isLastPage = (command.page + 1) >= totalPages

        return@withReadOnly PageResult(
            content = books,
            totalPages = totalPages,
            totalElements = totalElements,
            isLastPage = isLastPage
        )
    }

    override suspend fun loadRecentBooks(command: PageCommand): PageResult<RecentBookSortResult>? = dbProtection.withReadOnly {
        val size = command.size
        val start = command.offset
        val end = start + size - 1

        val jsonList = cacheRedisTemplate.opsForList().range(RECENT_BOOK_KEY, start, end)

        if (jsonList.isNullOrEmpty()) return@withReadOnly null

        val books = jsonList.map { objectMapper.readValue(it as String, RecentBookSortResult::class.java) }

        val totalElements = cacheRedisTemplate.opsForList().size(RECENT_BOOK_KEY) ?: 0L
        val totalPages = ceil(totalElements.toDouble() / size).toInt()
        val isLastPage = (command.page + 1) >= totalPages

        return@withReadOnly PageResult(
            content = books,
            totalElements = totalElements,
            totalPages = totalPages,
            isLastPage = isLastPage,
        )
    }

    override suspend fun loadEvents(): List<EventInfoResult>? = dbProtection.withReadOnly {
        val jsonList = cacheRedisTemplate.opsForList().range(EVENT_KEY, 0, -1)

        if (jsonList.isNullOrEmpty()) return@withReadOnly null

        val events = jsonList.map { objectMapper.readValue(it as String, EventInfoResult::class.java) }

        return@withReadOnly events
    }
}