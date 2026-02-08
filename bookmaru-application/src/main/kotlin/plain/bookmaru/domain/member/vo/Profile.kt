package plain.bookmaru.domain.member.vo

import java.time.LocalDateTime

data class Profile(
    val nickname: String,
    val profileImage: String? = null,
    val oneMonthStatics: Int? = 0,
    val overdueTerm: LocalDateTime? = null,
    val bookReadTime: LocalDateTime? = null,
) {
}