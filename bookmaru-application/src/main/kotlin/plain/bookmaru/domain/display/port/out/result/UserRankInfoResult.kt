package plain.bookmaru.domain.display.port.out.result

import kotlinx.serialization.Serializable

@Serializable
data class UserRankInfoResult(
    val memberId: Long,
    val rank: Int,
    val nickName: String,
    val oneMonthStatistics: Int,
    val profileImage: String,
    val affiliationName: String
)