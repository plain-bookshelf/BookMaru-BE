package plain.bookmaru.domain.inventory.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.port.out.result.BookDetailInfoResult

interface BookAffiliationPort {
    suspend fun findPopularSort(command: PageCommand, affiliationId: Long) : SliceResult<BookAffiliation>
    suspend fun findRecentSort(command: PageCommand, affiliationId: Long) : SliceResult<BookAffiliation>
    suspend fun findById(id: Long): BookAffiliation?
    suspend fun findBookInfoByBookId(bookAffiliationId: Long, affiliationId: Long, memberId: Long): BookDetailInfoResult?

    fun incrementLikeCount(bookAffiliationId: Long)
    fun decrementLikeCount(bookAffiliationId: Long)
}