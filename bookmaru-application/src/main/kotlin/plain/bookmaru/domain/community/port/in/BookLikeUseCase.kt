package plain.bookmaru.domain.community.port.`in`

import plain.bookmaru.domain.community.port.`in`.command.BookLikeCommand

interface BookLikeUseCase {
    suspend fun execute(command: BookLikeCommand)
}