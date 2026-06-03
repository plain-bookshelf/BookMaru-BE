package plain.bookmaru.domain.community.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.community.vo.BookReact

@Aggregate
class Comment(
    val id: Long? = null,
    val memberId: Long,
    val bookAffiliationId: Long,
    bookReact: BookReact,
    val likeCount: Int = 0
) {
    var bookReact = bookReact
        private set

    companion object {
        fun createComment(
            memberId: Long,
            bookAffiliationId: Long,
            bookReact: BookReact
        ): Comment {
            return Comment(
                memberId = memberId,
                bookAffiliationId = bookAffiliationId,
                bookReact = BookReact(
                    comment = bookReact.comment
                )
            )
        }
    }

    fun modifyComment(bookReact: BookReact) {
        this.bookReact = bookReact
    }
}