package plain.bookmaru.domain.community.presentation.dto.request

import plain.bookmaru.domain.community.port.`in`.command.BookCommentChangeCommand
import plain.bookmaru.domain.community.port.`in`.command.BookCommentWriteCommand

data class BookCommentRequestDto(
    val comment: String,
    val starCount: Int
) {
    fun toWriteCommand(bookAffiliationId: Long, memberId: Long) : BookCommentWriteCommand
        = BookCommentWriteCommand(
        memberId = memberId,
        bookAffiliationId = bookAffiliationId,
        comment = comment,
        starCount = starCount
    )

    fun toChangeCommand(commentId: Long, memberId: Long) : BookCommentChangeCommand
        = BookCommentChangeCommand(
        memberId = memberId,
        commentId = commentId,
        comment = comment,
        starCount = starCount
    )
}