package plain.bookmaru.domain.community.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.community.model.Comment
import plain.bookmaru.domain.community.port.`in`.BookCommentWriteUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookCommentWriteCommand
import plain.bookmaru.domain.community.port.out.CommentPort
import plain.bookmaru.domain.community.vo.BookReact

@Service
class BookCommentWriteService(
    private val commentPort: CommentPort
) : BookCommentWriteUseCase {
    override suspend fun execute(command: BookCommentWriteCommand) {
        val comment = Comment.createComment(
            command.memberId,
            command.bookAffiliationId,
            BookReact(
                comment = command.comment,
                starCount = command.starCount
            )
        )

        commentPort.save(comment, command.bookAffiliationId, command.memberId)
    }
}