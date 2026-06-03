package plain.bookmaru.domain.community.model

import plain.bookmaru.common.annotation.Aggregate

@Aggregate
class CommentLike(
    val memberId: Long,
    val commentId: Long
)