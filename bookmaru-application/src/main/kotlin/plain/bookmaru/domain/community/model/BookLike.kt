package plain.bookmaru.domain.community.model

import plain.bookmaru.common.annotation.Aggregate

@Aggregate
class BookLike(
    val memberId: Long,
    val bookId: Long,
    val status: Boolean
)