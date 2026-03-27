package plain.bookmaru.domain.community.presentation.dto.request

import plain.bookmaru.domain.community.port.`in`.command.BookCommentChangeCommand
import plain.bookmaru.domain.community.port.`in`.command.BookCommentWriteCommand

data class BookCommentRequestDto(
    val comment: String
) {
    fun toWriteCommand(bookAffiliationId: Long, memberId: Long) : BookCommentWriteCommand
        = BookCommentWriteCommand(
        memberId = memberId,
        bookAffiliationId = bookAffiliationId,
        comment = comment
    )

    fun toChangeCommand(commentId: Long, memberId: Long) : BookCommentChangeCommand
        = BookCommentChangeCommand(
        memberId = memberId,
        commentId = commentId,
        comment = comment
    )
}