package plain.bookmaru.domain.community.port.`in`.command

data class CommentLikeCommand(
    val memberId: Long,
    val commentId: Long
)
