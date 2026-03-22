package plain.bookmaru.domain.community.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.community.exception.NotMatchWriterMemberException
import plain.bookmaru.domain.community.port.`in`.BookCommentDeleteUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookCommentDeleteCommand
import plain.bookmaru.domain.community.port.out.CommentPort

@Service
class BookCommentDeleteService(
    private val commentPort: CommentPort
) : BookCommentDeleteUseCase {
    override suspend fun execute(command: BookCommentDeleteCommand) {
        val comment = commentPort.findByCommentId(command.commentId)

        if (comment.memberId != command.memberId) {
            throw NotMatchWriterMemberException("${comment.memberId} 아이디를 가진 유저가 다른 유저의 댓글 정보를 수정하려고 시도했습니다.")
        }

        commentPort.delete(command.commentId)
    }
}