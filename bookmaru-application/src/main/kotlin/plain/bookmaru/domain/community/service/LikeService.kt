package plain.bookmaru.domain.community.service

import plain.bookmaru.common.annotation.Service
import plain.bookmaru.domain.book.exception.NotFoundBookException
import plain.bookmaru.domain.community.exception.AlreadyLikedException
import plain.bookmaru.domain.community.model.BookLike
import plain.bookmaru.domain.community.port.`in`.BookLikeUseCase
import plain.bookmaru.domain.community.port.`in`.command.BookLikeCommand
import plain.bookmaru.domain.community.port.out.BookLikePort
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort

@Service
class LikeService(
    private val bookLikePort: BookLikePort,
    private val bookAffiliationPort: BookAffiliationPort
) : BookLikeUseCase {
    override suspend fun execute(command: BookLikeCommand) {
        val bookAffiliationId = command.bookAffiliationId
        val memberId = command.memberId

        val bookLike = bookLikePort.findByBookAffiliationIdAndMemberId(bookAffiliationId, memberId)

        if (bookLike != null)
            throw AlreadyLikedException("책 아이디: $bookAffiliationId 에서 $memberId 유저가 좋아요를 두 번 눌렀습니다.")

        val bookAffiliation = bookAffiliationPort.findById(bookAffiliationId)
            ?: throw NotFoundBookException("책 정보를 찾지 못 했습니다.")

        val newBookLike = BookLike(
            memberId = memberId,
            bookAffiliationId = bookAffiliationId
        )

        bookLikePort.save(newBookLike)

        bookAffiliation.modifyLikeCount()
        bookAffiliationPort.update(bookAffiliation)
    }
}