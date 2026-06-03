package plain.bookmaru.domain.community.port.`in`.command

import plain.bookmaru.domain.community.vo.BookReact

data class BookCommentChangeCommand(
    val memberId: Long,
    val commentId: Long,
    val bookReact: BookReact
)
