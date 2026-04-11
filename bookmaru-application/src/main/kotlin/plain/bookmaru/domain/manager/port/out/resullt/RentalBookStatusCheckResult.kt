package plain.bookmaru.domain.manager.port.out.resullt

import java.time.LocalDate

data class RentalBookStatusCheckResult(
    val memberId: Long,
    val bookDetailId: Long,
    val title: String,
    val publisher: String,
    val nickname: String,
    val callNumber: String,
    val returnDate: LocalDate,
    val isOverdue: Boolean
)
