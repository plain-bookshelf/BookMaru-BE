package plain.bookmaru.domain.community.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.TransactionPort
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

private val log = KotlinLogging.logger {}

@Service
class LikeService(
    private val bookLikePort: BookLikePort,
    private val bookAffiliationPort: BookAffiliationPort,
    private val commentLikePort: CommentLikePort,
    private val commentPort: CommentPort,
    private val transactionPort: TransactionPort
) : BookLikeUseCase, CommentLikeUseCase {
    override suspend fun execute(command: BookLikeCommand) {
        val bookAffiliationId = command.bookAffiliationId
        val memberId = command.memberId

        val bookLike = bookLikePort.findByBookAffiliationIdAndMemberId(bookAffiliationId, memberId)

        if (bookLike != null)
            throw AlreadyLikedException("bookAffiliationId: $bookAffiliationId 에서 memberId: $memberId 유저가 좋아요를 두 번 눌렀습니다.")

        val newBookLike = BookLike(
            memberId = memberId,
            bookAffiliationId = bookAffiliationId
        )

        transactionPort.withTransaction {
            bookLikePort.save(newBookLike)
            log.info { "bookAffiliationId: $bookAffiliationId 좋아요 데이터를 추가하는데 성공했습니다." }

            bookAffiliationPort.incrementLikeCount(bookAffiliationId)
            log.info { "bookAffiliationId: $bookAffiliationId 좋아요를 증가시키는데 성공했습니다." }
        }
    }

    override suspend fun execute(command: CommentLikeCommand) {
        val memberId = command.memberId
        val commentId = command.commentId

        val commentLike = commentLikePort.findByCommentIdAndMemberId(commentId, memberId)

        if (commentLike != null)
            throw AlreadyLikedException("commentId: $commentId 에서 memberId: $memberId 유저가 이미 좋아요를 눌렀습니다.")

        val newCommentLike = CommentLike(
            memberId = memberId,
            commentId = commentId
        )

        transactionPort.withTransaction {
            commentLikePort.save(newCommentLike)
            log.info { "commentId: $commentId 좋아요 데이터를 추가하는데 성공했습니다." }

            commentPort.incrementLikeCount(commentId)
            log.info { "commentId: $commentId 좋아요를 증가시키는데 성공했습니다." }
        }
    }
}