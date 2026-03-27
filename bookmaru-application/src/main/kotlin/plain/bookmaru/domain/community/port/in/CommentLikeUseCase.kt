package plain.bookmaru.domain.community.port.`in`

import plain.bookmaru.domain.community.port.`in`.command.CommentLikeCommand

interface CommentLikeUseCase {
    suspend fun commentLike(command: CommentLikeCommand)
}