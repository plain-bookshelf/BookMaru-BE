package plain.bookmaru.domain.community.port.out

import plain.bookmaru.domain.community.model.CommentLike

interface CommentLikePort {
    suspend fun findByCommentIdAndMemberId(commentId: Long, memberId: Long) : CommentLike?

    fun save(commentLike: CommentLike)

    fun delete(commentLike: CommentLike)
}