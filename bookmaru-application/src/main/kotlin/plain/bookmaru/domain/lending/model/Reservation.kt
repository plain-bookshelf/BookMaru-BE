package plain.bookmaru.domain.lending.model

import plain.bookmaru.common.annotation.Aggregate

@Aggregate
class Reservation(
    val waitingRank: Int
)