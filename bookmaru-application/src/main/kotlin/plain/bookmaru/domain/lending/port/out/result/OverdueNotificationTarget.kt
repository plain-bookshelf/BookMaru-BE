package plain.bookmaru.domain.lending.port.out.result

import java.time.LocalDate

data class OverdueNotificationTarget(
    val memberId: Long,
    val bookDetailId: Long,
    val bookAffiliationId: Long,
    val title: String,
    val returnDate: LocalDate
)
