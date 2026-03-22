package plain.bookmaru.domain.community.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.community.exception.NotMatchWriterMemberException
import plain.bookmaru.domain.community.port.`in`.BookCommentChangeUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookCommentChangeCommand
import plain.bookmaru.domain.community.port.out.CommentPort

@Service
class BookCommentChangeService(
    private val commentPort: CommentPort
) : BookCommentChangeUseCase {

    override suspend fun execute(command: BookCommentChangeCommand) {
        val comment = commentPort.findByCommentId(command.commentId)

        if (comment.memberId != command.memberId) {
            throw NotMatchWriterMemberException("${comment.memberId} 아이디를 가진 유저가 다른 유저의 댓글 정보를 수정하려고 시도했습니다.")
        }

        commentPort.save(comment, comment.bookAffiliationId, command.memberId)
    }
}