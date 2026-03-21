package plain.bookmaru.domain.community.model

import plain.bookmaru.common.annotation.Aggregate

@Aggregate
class BookLike(
    val memberId: Long,
    val bookAffiliationId: Long,
    val status: Boolean
)