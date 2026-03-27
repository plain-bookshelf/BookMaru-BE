package plain.bookmaru.domain.community.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.community.model.Comment
import plain.bookmaru.domain.display.port.out.result.CommentResult

interface CommentPort {
    suspend fun findByBookAffiliationId(bookAffiliationId: Long, pageCommand: PageCommand): SliceResult<CommentResult>
    suspend fun findById(commentId: Long) : Comment
    suspend fun save(comment: Comment, bookAffiliationId: Long?, memberId: Long?): Comment
    suspend fun delete(commentId: Long)

    suspend fun incrementLikeCount(commentId: Long)
}