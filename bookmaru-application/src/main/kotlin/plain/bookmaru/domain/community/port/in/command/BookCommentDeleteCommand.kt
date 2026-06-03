package plain.bookmaru.domain.community.port.`in`.command

data class BookCommentDeleteCommand(
    val commentId: Long,
    val memberId: Long
)
