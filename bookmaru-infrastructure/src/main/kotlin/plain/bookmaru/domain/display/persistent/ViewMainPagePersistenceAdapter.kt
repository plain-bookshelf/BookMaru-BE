package plain.bookmaru.domain.display.persistent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.PopularBookSortResult
import plain.bookmaru.domain.display.port.out.result.RecentBookSortResult
import java.time.Duration
import kotlin.math.ceil

private const val EVENT_KEY = "cache:display:main:event"
private const val POPULAR_BOOK_KEY = "cache:display:main:popular"
private const val RECENT_BOOK_KEY = "cache:display:main:recent"

private val EVENT_TTL = Duration.ofHours(24)
private val POPULAR_BOOK_TTL = Duration.ofMinutes(10)
private val RECENT_BOOK_TTL = Duration.ofHours(24)

@Component
class ViewMainPagePersistenceAdapter(
    @Qualifier("cacheRedisTemplate")
    private val cacheRedisTemplate: RedisTemplate<String, ByteArray>,
    @Qualifier("virtualDispatcher") private val virtualDispatcher: CoroutineDispatcher
) : MainPagePort {
    /*
    SAVE
     */
    override suspend fun savePopularBooks(books: List<PopularBookSortResult>, platformType: PlatformType, affiliationId: Long): Unit = withContext(virtualDispatcher) {
            if (books.isEmpty()) return@withContext

            val key = "$POPULAR_BOOK_KEY:$platformType:affiliationId$affiliationId"
            val byteArray = books.map { ProtoBuf.encodeToByteArray(it) }

            cacheRedisTemplate.delete(key)
            cacheRedisTemplate.opsForList().rightPushAll(key, byteArray)
            cacheRedisTemplate.expire(key, POPULAR_BOOK_TTL)
        }

    override suspend fun saveRecentBooks(books: List<RecentBookSortResult>, platformType: PlatformType, affiliationId: Long): Unit = withContext(virtualDispatcher) {
        if (books.isEmpty()) return@withContext

        val key = "$RECENT_BOOK_KEY:$platformType:affiliationId$affiliationId"
        val byteArray = books.map { ProtoBuf.encodeToByteArray(it) }

        cacheRedisTemplate.delete(key)
        cacheRedisTemplate.opsForList().rightPushAll(key, byteArray)
        cacheRedisTemplate.expire(key, RECENT_BOOK_TTL)
    }

    override suspend fun saveEvents(events: List<EventInfoResult>, affiliationId: Long): Unit = withContext(virtualDispatcher) {
        if (events.isEmpty()) return@withContext

        val key = "$EVENT_KEY:affiliationId$affiliationId"
        val byteArray = events.map { ProtoBuf.encodeToByteArray(it) }

        cacheRedisTemplate.delete(key)
        cacheRedisTemplate.opsForList().rightPushAll(key, byteArray)
        cacheRedisTemplate.expire(key, EVENT_TTL)
    }
    /*
    LOAD
     */

    override suspend fun loadPopularBooks(command: PageCommand, platformType: PlatformType, affiliationId: Long): SliceResult<PopularBookSortResult>? = withContext(virtualDispatcher) {
        val key = "$POPULAR_BOOK_KEY:$platformType:affiliationId$affiliationId"

        return@withContext loadBookInfo(key, command)
    }

    override suspend fun loadRecentBooks(command: PageCommand, platformType: PlatformType, affiliationId: Long): SliceResult<RecentBookSortResult>? = withContext(virtualDispatcher) {
        val key = "$RECENT_BOOK_KEY:$platformType:affiliationId$affiliationId"

        return@withContext loadBookInfo(key, command)
    }

    override suspend fun loadEvents(affiliationId: Long): List<EventInfoResult>? = withContext(virtualDispatcher) {
        val key = "$EVENT_KEY:affiliationId$affiliationId"
        val rangeResult = cacheRedisTemplate.opsForList().range(key, 0, -1)

        if (rangeResult.isNullOrEmpty()) return@withContext null

        val events = rangeResult.map { ProtoBuf.decodeFromByteArray<EventInfoResult>(it) }

        return@withContext events
    }

    private inline fun <reified T> loadBookInfo(key: String, command: PageCommand): SliceResult<T>? {
        val size = command.size
        val start = command.offset
        val end = start + size - 1

        val rangeResult = cacheRedisTemplate.opsForList().range(key, start, end)

        if (rangeResult.isNullOrEmpty()) return null

        val books = rangeResult.map {
            ProtoBuf.decodeFromByteArray<T>(it)
        }

        val totalElements = cacheRedisTemplate.opsForList().size(key) ?: 0L
        val totalPages = ceil(totalElements.toDouble() / size).toInt()
        val isLastPage = (command.page + 1) >= totalPages

        return SliceResult(
            content = books,
            isLastPage = isLastPage,
        )
    }
}