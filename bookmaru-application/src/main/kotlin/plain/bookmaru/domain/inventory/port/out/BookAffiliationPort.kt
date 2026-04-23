package plain.bookmaru.domain.inventory.port.out

import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.port.out.result.BookDetailInfoResult

interface BookAffiliationPort {
    suspend fun findPopularSort(affiliationId: Long) : List<BookAffiliation>
    suspend fun findRecentSort(affiliationId: Long) : List<BookAffiliation>
    suspend fun findById(id: Long): BookAffiliation?
    suspend fun findBookInfoByBookId(bookAffiliationId: Long, affiliationId: Long, memberId: Long): BookDetailInfoResult?

    fun incrementLikeCount(bookAffiliationId: Long)
    fun decrementLikeCount(bookAffiliationId: Long)
    fun decrementReservationCount(bookAffiliationId: Long)
}