package plain.bookmaru.domain.community.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.community.exception.NoLikedException
import plain.bookmaru.domain.community.port.`in`.BookUnLikeUseCase
import plain.bookmaru.domain.community.port.`in`.CommentUnLikeUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookLikeCommand
import plain.bookmaru.domain.community.port.`in`.command.CommentLikeCommand
import plain.bookmaru.domain.community.port.out.BookLikePort
import plain.bookmaru.domain.community.port.out.CommentLikePort
import plain.bookmaru.domain.community.port.out.CommentPort
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort

private val log = KotlinLogging.logger {}

@Service
class UnLikeService(
    private val bookLikePort: BookLikePort,
    private val commentLikePort: CommentLikePort,
    private val commentPort: CommentPort,
    private val bookAffiliationPort: BookAffiliationPort,
    private val transactionPort: TransactionPort
) : BookUnLikeUseCase, CommentUnLikeUseCase {
    override suspend fun execute(command: BookLikeCommand) {
        val bookAffiliationId = command.bookAffiliationId
        val memberId = command.memberId

        val bookLike = bookLikePort.findByBookAffiliationIdAndMemberId(bookAffiliationId, memberId)

        if (bookLike == null)
            throw NoLikedException("memberId: $memberId 유저가 기존에 bookAffiliationId: $bookAffiliationId 아이디의 책에 좋아요를 누르지 않았습니다.")

        transactionPort.withTransaction {
            bookLikePort.delete(bookLike)
            log.info { "bookAffiliationId: $bookAffiliationId 좋아요 데이터를 지우는데 성공했습니다." }

            bookAffiliationPort.decrementLikeCount(bookAffiliationId)
            log.info { "bookAffiliationId: $bookAffiliationId 좋아요를 감소시키는데 성공했습니다." }
        }
    }

    override suspend fun execute(command: CommentLikeCommand) {
        val commentId = command.commentId
        val memberId = command.memberId

        val commentLike = commentLikePort.findByCommentIdAndMemberId(commentId, memberId)

        if (commentLike == null)
            throw NoLikedException("memberId: $memberId 유저가 기존에 commentId: $commentId 아이디의 댓글에 좋아요를 누르지 않았습니다.")

        transactionPort.withTransaction {
            commentLikePort.delete(commentLike)
            log.info { "commentId: $commentId 좋아요 데이터를 지우는데 성공했습니다." }

            commentPort.decrementLikeCount(commentId)
            log.info { "commentId: $commentId 좋아요를 감소시키는데 성공했습니다." }
        }
    }
}