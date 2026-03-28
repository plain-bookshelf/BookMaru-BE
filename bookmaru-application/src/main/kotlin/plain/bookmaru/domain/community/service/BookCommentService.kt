package plain.bookmaru.domain.community.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.community.exception.NotMatchWriterMemberException
import plain.bookmaru.domain.community.model.Comment
import plain.bookmaru.domain.community.port.`in`.BookCommentChangeUseCase
import plain.bookmaru.domain.community.port.`in`.BookCommentDeleteUseCase
import plain.bookmaru.domain.community.port.`in`.BookCommentWriteUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookCommentChangeCommand
import plain.bookmaru.domain.community.port.`in`.command.BookCommentDeleteCommand
import plain.bookmaru.domain.community.port.`in`.command.BookCommentWriteCommand
import plain.bookmaru.domain.community.port.out.CommentPort
import plain.bookmaru.domain.community.vo.BookReact

@Service
class BookCommentService(
    private val commentPort: CommentPort
) : BookCommentWriteUseCase, BookCommentDeleteUseCase, BookCommentChangeUseCase {
    override suspend fun execute(command: BookCommentWriteCommand) {
        val comment = Comment.createComment(
            command.memberId,
            command.bookAffiliationId,
            BookReact(
                comment = command.bookReact.comment
            )
        )

        commentPort.save(comment, command.bookAffiliationId, command.memberId)
    }

    override suspend fun execute(command: BookCommentDeleteCommand) {
        val comment = commentPort.findById(command.commentId)

        if (comment.memberId != command.memberId) {
            throw NotMatchWriterMemberException("${command.memberId} 아이디를 가진 유저가 다른 유저의 댓글 정보를 수정하려고 시도했습니다.")
        }

        commentPort.delete(command.commentId)
    }

    override suspend fun execute(command: BookCommentChangeCommand) {
        val comment = commentPort.findById(command.commentId)

        if (comment.memberId != command.memberId) {
            throw NotMatchWriterMemberException("${command.memberId} 아이디를 가진 유저가 다른 유저의 댓글 정보를 수정하려고 시도했습니다.")
        }

        comment.modifyComment(command.bookReact)
        commentPort.save(comment, comment.bookAffiliationId, command.memberId)
    }
}