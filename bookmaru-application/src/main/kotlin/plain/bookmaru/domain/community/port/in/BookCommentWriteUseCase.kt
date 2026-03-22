package plain.bookmaru.domain.community.port.`in`

import plain.bookmaru.domain.community.port.`in`.command.BookCommentWriteCommand

interface BookCommentWriteUseCase {
    suspend fun execute(command: BookCommentWriteCommand)
}