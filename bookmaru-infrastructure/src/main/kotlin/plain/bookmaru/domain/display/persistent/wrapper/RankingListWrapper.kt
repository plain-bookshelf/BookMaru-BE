package plain.bookmaru.domain.display.persistent.wrapper

import kotlinx.serialization.Serializable
import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult

@Serializable
data class RankingListWrapper(val ranking: List<UserRankInfoResult>)