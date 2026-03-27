package plain.bookmaru.domain.community.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.community.exception.AlreadyLikedException
import plain.bookmaru.domain.community.model.BookLike
import plain.bookmaru.domain.community.model.CommentLike
import plain.bookmaru.domain.community.port.`in`.CommentLikeUseCase
import plain.bookmaru.domain.community.port.`in`.BookLikeUseCase
import plain.bookmaru.domain.community.port.`in`.command.CommentLikeCommand
import plain.bookmaru.domain.community.port.`in`.command.BookLikeCommand
import plain.bookmaru.domain.community.port.out.BookLikePort
import plain.bookmaru.domain.community.port.out.CommentLikePort
import plain.bookmaru.domain.community.port.out.CommentPort
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort

@Service
class LikeService(
    private val bookLikePort: BookLikePort,
    private val bookAffiliationPort: BookAffiliationPort,
    private val commentLikePort: CommentLikePort,
    private val commentPort: CommentPort
) : BookLikeUseCase, CommentLikeUseCase {
    override suspend fun execute(command: BookLikeCommand) {
        val bookAffiliationId = command.bookAffiliationId
        val memberId = command.memberId

        val bookLike = bookLikePort.findByBookAffiliationIdAndMemberId(bookAffiliationId, memberId)

        if (bookLike != null)
            throw AlreadyLikedException("책 아이디: $bookAffiliationId 에서 $memberId 유저가 좋아요를 두 번 눌렀습니다.")

        val newBookLike = BookLike(
            memberId = memberId,
            bookAffiliationId = bookAffiliationId
        )

        bookLikePort.save(newBookLike)

        bookAffiliationPort.incrementLikeCount(bookAffiliationId)
    }

    override suspend fun commentLike(command: CommentLikeCommand) {
        val memberId = command.memberId
        val commentId = command.commentId

        val commentLike = commentLikePort.findByCommentIdAndMemberId(memberId, commentId)

        if (commentLike != null)
            throw AlreadyLikedException("댓글 아이디: $commentId 에서 $memberId 유저가 이미 좋아요를 눌렀습니다.")

        val newCommentLike = CommentLike(
            memberId = memberId,
            commentId = commentId
        )

        commentLikePort.save(newCommentLike)

        commentPort.incrementLikeCount(commentId)
    }
}