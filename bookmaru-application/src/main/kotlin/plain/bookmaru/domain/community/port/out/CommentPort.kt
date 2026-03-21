package plain.bookmaru.domain.community.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.display.port.out.result.CommentResult

interface CommentPort {
    suspend fun findByBookAffiliationId(bookAffiliationId: Long, pageCommand: PageCommand): SliceResult<CommentResult>
}