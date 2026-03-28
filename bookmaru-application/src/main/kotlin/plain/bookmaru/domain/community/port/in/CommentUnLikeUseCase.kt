package plain.bookmaru.domain.community.port.`in`

import plain.bookmaru.domain.community.port.`in`.command.CommentLikeCommand

interface CommentUnLikeUseCase {
    suspend fun execute(command: CommentLikeCommand)
}