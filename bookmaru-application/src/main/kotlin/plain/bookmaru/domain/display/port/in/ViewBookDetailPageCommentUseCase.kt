package plain.bookmaru.domain.display.port.`in`

import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.display.port.`in`.command.ViewBookDetailPageCommentCommand
import plain.bookmaru.domain.display.port.out.result.CommentResult

interface ViewBookDetailPageCommentUseCase {
    suspend fun execute(command: ViewBookDetailPageCommentCommand): SliceResult<CommentResult>
}