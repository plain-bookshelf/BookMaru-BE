package plain.bookmaru.domain.display.persistent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.persistent.wrapper.EventListWrapper
import plain.bookmaru.domain.display.persistent.wrapper.BookListWrapper
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import plain.bookmaru.domain.display.port.out.result.BookSortResult
import java.time.Duration

private const val EVENT_KEY = "cache:display:main:event"
private const val POPULAR_BOOK_KEY = "cache:display:main:popular"
private const val RECENT_BOOK_KEY = "cache:display:main:recent"

private val EVENT_TTL = Duration.ofHours(24)
private val POPULAR_BOOK_TTL = Duration.ofHours(1)
private val RECENT_BOOK_TTL = Duration.ofHours(24)

@Component
class ViewMainPagePersistenceAdapter(
    @Qualifier("cacheRedisTemplate")
    private val cacheRedisTemplate: RedisTemplate<String, ByteArray>,
    @Qualifier("virtualDispatcher")
    private val virtualDispatcher: CoroutineDispatcher
) : MainPagePort {
    /*
    SAVE
     */

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun savePopularBooks(books: List<BookSortResult>, platformType: PlatformType, affiliationId: Long): Unit = withContext(virtualDispatcher) {
            val key = "$POPULAR_BOOK_KEY:$platformType:affiliationId$affiliationId"
            if (validIsNotEmptyBook(books, key)) return@withContext

            val byteArray = ProtoBuf.encodeToByteArray(BookListWrapper(books))
            cacheRedisTemplate.opsForValue().set(key, byteArray, POPULAR_BOOK_TTL)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun saveRecentBooks(books: List<BookSortResult>, platformType: PlatformType, affiliationId: Long): Unit = withContext(virtualDispatcher) {
        val key = "$RECENT_BOOK_KEY:$platformType:affiliationId$affiliationId"
        if (validIsNotEmptyBook(books, key)) return@withContext

        val byteArray = ProtoBuf.encodeToByteArray(BookListWrapper(books))
        cacheRedisTemplate.opsForValue().set(key, byteArray, RECENT_BOOK_TTL)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun saveEvents(events: List<EventInfoResult>?, affiliationId: Long): Unit = withContext(virtualDispatcher) {
        if (events == null) return@withContext

        val key = "$EVENT_KEY:affiliationId$affiliationId"
        if (validIsNotEmptyBook(events, key)) return@withContext

        val byteArray = ProtoBuf.encodeToByteArray(EventListWrapper(events))
        cacheRedisTemplate.opsForValue().set(key, byteArray, EVENT_TTL)
    }
    /*
    LOAD
     */

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadPopularBooks(platformType: PlatformType, affiliationId: Long): List<BookSortResult>? = withContext(virtualDispatcher) {
        val key = "$POPULAR_BOOK_KEY:$platformType:affiliationId$affiliationId"

        val byteArray = cacheRedisTemplate.opsForValue().get(key) ?: return@withContext null

        val allBooks = ProtoBuf.decodeFromByteArray<BookListWrapper>(byteArray).books
        return@withContext allBooks
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadRecentBooks(platformType: PlatformType, affiliationId: Long): List<BookSortResult>? = withContext(virtualDispatcher) {
        val key = "$RECENT_BOOK_KEY:$platformType:affiliationId$affiliationId"

        val byteArray = cacheRedisTemplate.opsForValue().get(key) ?: return@withContext null

        val allBooks = ProtoBuf.decodeFromByteArray<BookListWrapper>(byteArray).books
        return@withContext allBooks
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadEvents(affiliationId: Long): List<EventInfoResult>? = withContext(virtualDispatcher) {
        val key = "$EVENT_KEY:affiliationId$affiliationId"

        val byteArray = cacheRedisTemplate.opsForValue().get(key) ?: return@withContext null

        val allEvents = ProtoBuf.decodeFromByteArray<EventListWrapper>(byteArray).events
        return@withContext allEvents
    }
    /*
    private helper method
     */

    private fun <T> validIsNotEmptyBook(books: List<T>, key: String): Boolean {
        if (books.isEmpty()) {
            cacheRedisTemplate.delete(key)
            return true
        }
        return false
    }
}