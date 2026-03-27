package plain.bookmaru.domain.community.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.community.exception.NoLikedException
import plain.bookmaru.domain.community.port.`in`.BookUnLikeUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookLikeCommand
import plain.bookmaru.domain.community.port.out.BookLikePort
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort

@Service
class UnLikeService(
    private val bookLikePort: BookLikePort,
    private val bookAffiliationPort: BookAffiliationPort
) : BookUnLikeUseCase {
    override suspend fun bookUnLike(command: BookLikeCommand) {
        val bookAffiliationId = command.bookAffiliationId
        val memberId = command.memberId

        val bookLike = bookLikePort.findByBookAffiliationIdAndMemberId(memberId, bookAffiliationId)

        if (bookLike == null)
            throw NoLikedException("$memberId 유저가 기존에 $bookAffiliationId 아이디의 책에 좋아요를 누르지 않았습니다.")

        bookLikePort.delete(bookLike)
        bookAffiliationPort.decrementLikeCount(bookAffiliationId)
    }
}