package plain.bookmaru.domain.community.port.out

import plain.bookmaru.domain.community.model.BookLike

interface BookLikePort {
    suspend fun findByBookAffiliationIdAndMemberId(bookAffiliationId: Long, memberId: Long) : BookLike?

    suspend fun save(bookLike: BookLike)
}