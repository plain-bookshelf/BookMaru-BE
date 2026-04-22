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
import plain.bookmaru.domain.display.persistent.wrapper.RankingListWrapper
import plain.bookmaru.domain.display.port.out.RankingPagePort
import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult
import java.time.Duration

private const val RANKING_KEY = "cache:display:ranking"

private val RANKING_TTL = Duration.ofHours(12)

@Component
class ViewRankingPagePersistenceAdapter(
    @Qualifier("virtualDispatcher")
    private val virtualDispatcher: CoroutineDispatcher,
    @Qualifier("cacheRedisTemplate")
    private val cacheRedisTemplate: RedisTemplate<String, ByteArray>
): RankingPagePort {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun saveRanking(ranking: List<UserRankInfoResult>, affiliationId: Long) = withContext(virtualDispatcher) {
        val key = "$RANKING_KEY:affiliationId:$affiliationId"
        if (ranking.isEmpty()) {
            cacheRedisTemplate.delete(key)
            return@withContext
        }

        val byteArray = ProtoBuf.encodeToByteArray(RankingListWrapper(ranking))
        cacheRedisTemplate.opsForValue().set(key, byteArray, RANKING_TTL)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadRankingPage(affiliationId: Long): List<UserRankInfoResult>? = withContext(virtualDispatcher) {
        val key = "$RANKING_KEY:affiliationId:$affiliationId"

        val byteArray = cacheRedisTemplate.opsForValue().get(key) ?: return@withContext null
        val ranking = runCatching {
            ProtoBuf.decodeFromByteArray<RankingListWrapper>(byteArray).ranking
        }.getOrElse {
            cacheRedisTemplate.delete(key)
            return@withContext null
        }

        return@withContext ranking
    }
}