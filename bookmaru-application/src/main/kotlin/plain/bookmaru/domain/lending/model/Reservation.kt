package plain.bookmaru.domain.lending.model

import plain.bookmaru.common.annotation.Aggregate

@Aggregate
class Reservation(
    val bookAffiliationId: Long,
    val memberId: Long,
    val waitingRank: Int
)