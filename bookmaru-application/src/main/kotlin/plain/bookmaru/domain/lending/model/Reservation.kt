package plain.bookmaru.domain.lending.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.member.model.Member

@Aggregate
class Reservation(
    val bookAffiliationId: Long,
    val member: Member,
    val waitingRank: Int
)