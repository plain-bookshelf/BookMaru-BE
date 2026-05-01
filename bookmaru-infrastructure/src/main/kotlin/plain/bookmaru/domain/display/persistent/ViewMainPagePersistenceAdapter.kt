package plain.bookmaru.domain.display.persistent

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.display.persistent.wrapper.BookListWrapper
import plain.bookmaru.domain.display.persistent.wrapper.EventListWrapper
import plain.bookmaru.domain.display.port.out.MainPagePort
import plain.bookmaru.domain.display.port.out.result.BookSortResult
import plain.bookmaru.domain.display.port.out.result.EventInfoResult
import java.time.Duration

@Component
class ViewMainPagePersistenceAdapter(
    @Qualifier("cacheRedisTemplate")
    private val cacheRedisTemplate: RedisTemplate<String, ByteArray>
) : MainPagePort {

    companion object {
        private const val EVENT_KEY = "cache:display:main:event"
        private const val POPULAR_BOOK_KEY = "cache:display:main:popular"
        private const val RECENT_BOOK_KEY = "cache:display:main:recent"

        private val EVENT_TTL = Duration.ofHours(24)
        private val POPULAR_BOOK_TTL = Duration.ofHours(1)
        private val RECENT_BOOK_TTL = Duration.ofHours(24)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun savePopularBooks(
        books: List<BookSortResult>,
        platformType: PlatformType,
        affiliationId: Long
    ) {
        val key = "$POPULAR_BOOK_KEY:$platformType:affiliationId$affiliationId"
        if (deleteWhenEmpty(books, key)) return

        val byteArray = ProtoBuf.encodeToByteArray(BookListWrapper(books))
        cacheRedisTemplate.opsForValue().set(key, byteArray, POPULAR_BOOK_TTL)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun saveRecentBooks(
        books: List<BookSortResult>,
        platformType: PlatformType,
        affiliationId: Long
    ) {
        val key = "$RECENT_BOOK_KEY:$platformType:affiliationId$affiliationId"
        if (deleteWhenEmpty(books, key)) return

        val byteArray = ProtoBuf.encodeToByteArray(BookListWrapper(books))
        cacheRedisTemplate.opsForValue().set(key, byteArray, RECENT_BOOK_TTL)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun saveEvents(events: List<EventInfoResult>?, affiliationId: Long) {
        if (events == null) return

        val key = "$EVENT_KEY:affiliationId$affiliationId"
        if (deleteWhenEmpty(events, key)) return

        val byteArray = ProtoBuf.encodeToByteArray(EventListWrapper(events))
        cacheRedisTemplate.opsForValue().set(key, byteArray, EVENT_TTL)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadPopularBooks(
        platformType: PlatformType,
        affiliationId: Long
    ): List<BookSortResult>? {
        val key = "$POPULAR_BOOK_KEY:$platformType:affiliationId$affiliationId"
        val byteArray = cacheRedisTemplate.opsForValue().get(key) ?: return null
        return ProtoBuf.decodeFromByteArray<BookListWrapper>(byteArray).books
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadRecentBooks(
        platformType: PlatformType,
        affiliationId: Long
    ): List<BookSortResult>? {
        val key = "$RECENT_BOOK_KEY:$platformType:affiliationId$affiliationId"
        val byteArray = cacheRedisTemplate.opsForValue().get(key) ?: return null
        return ProtoBuf.decodeFromByteArray<BookListWrapper>(byteArray).books
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadEvents(affiliationId: Long): List<EventInfoResult>? {
        val key = "$EVENT_KEY:affiliationId$affiliationId"
        val byteArray = cacheRedisTemplate.opsForValue().get(key) ?: return null
        return ProtoBuf.decodeFromByteArray<EventListWrapper>(byteArray).events
    }

    private fun <T> deleteWhenEmpty(values: List<T>, key: String): Boolean {
        if (values.isEmpty()) {
            cacheRedisTemplate.delete(key)
            return true
        }
        return false
    }
}
