package plain.bookmaru.domain.community.port.`in`

import plain.bookmaru.domain.community.port.`in`.command.BookCommentChangeCommand

interface BookCommentChangeUseCase {
    suspend fun execute(command: BookCommentChangeCommand)
}