package plain.bookmaru.domain.community.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.community.vo.BookReact

@Aggregate
class Comment(
    val id: Long? = null,
    val memberId: String,
    val bookId: Long,
    val bookReact: BookReact,
    val likeCount: Int
)