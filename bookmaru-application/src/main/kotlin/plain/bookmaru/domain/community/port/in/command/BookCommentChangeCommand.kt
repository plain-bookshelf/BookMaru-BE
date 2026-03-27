package plain.bookmaru.domain.community.port.`in`.command

data class BookCommentChangeCommand(
    val memberId: Long,
    val commentId: Long,
    val comment: String
)
