package plain.bookmaru.domain.community.port.`in`.command

import plain.bookmaru.domain.community.vo.BookReact

data class BookCommentWriteCommand(
    val memberId: Long,
    val bookAffiliationId: Long,
    val bookReact: BookReact
)