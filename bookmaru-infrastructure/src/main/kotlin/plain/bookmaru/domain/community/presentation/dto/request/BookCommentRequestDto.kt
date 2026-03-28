package plain.bookmaru.domain.community.presentation.dto.request

import plain.bookmaru.domain.community.port.`in`.command.BookCommentChangeCommand
import plain.bookmaru.domain.community.port.`in`.command.BookCommentWriteCommand
import plain.bookmaru.domain.community.vo.BookReact

data class BookCommentRequestDto(
    val comment: String
) {
    fun toWriteCommand(bookAffiliationId: Long, memberId: Long) : BookCommentWriteCommand
        = BookCommentWriteCommand(
        memberId = memberId,
        bookAffiliationId = bookAffiliationId,
        bookReact = BookReact(comment)
    )

    fun toChangeCommand(commentId: Long, memberId: Long) : BookCommentChangeCommand
        = BookCommentChangeCommand(
        memberId = memberId,
        commentId = commentId,
        bookReact = BookReact(comment)
    )
}