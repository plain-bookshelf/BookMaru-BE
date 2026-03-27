package plain.bookmaru.domain.community.port.out

import plain.bookmaru.domain.community.model.CommentLike

interface CommentLikePort {
    suspend fun findByCommentIdAndMemberId(commentId: Long, memberId: Long) : CommentLike?
    suspend fun save(commentLike: CommentLike)
}