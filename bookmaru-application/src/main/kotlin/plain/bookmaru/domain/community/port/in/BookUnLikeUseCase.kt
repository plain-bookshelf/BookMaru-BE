package plain.bookmaru.domain.community.port.`in`

import plain.bookmaru.domain.community.port.`in`.command.BookLikeCommand

interface BookUnLikeUseCase {
    suspend fun execute(command: BookLikeCommand)
}