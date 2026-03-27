package plain.bookmaru.domain.display.port.out.result

data class CommentResult(
    val profileImage: String,
    val commentId: Long,
    val nickname: String,
    val comment: String,
    val likeCount: Int
)
