package plain.bookmaru.domain.community.port.`in`

import plain.bookmaru.domain.community.port.`in`.command.BookCommentDeleteCommand

interface BookCommentDeleteUseCase {
    suspend fun execute(command: BookCommentDeleteCommand)
}