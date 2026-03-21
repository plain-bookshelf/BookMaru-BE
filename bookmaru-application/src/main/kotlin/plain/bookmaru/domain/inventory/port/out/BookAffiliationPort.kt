package plain.bookmaru.domain.inventory.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.common.result.SliceResult
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.inventory.port.out.result.BookDetailInfoResult

interface BookAffiliationPort {
    suspend fun loadPopularSort(command: PageCommand, affiliationId: Long) : SliceResult<Book>
    suspend fun loadRecentSort(command: PageCommand, affiliationId: Long) : SliceResult<Book>
    suspend fun findById(id: Long): Book?
    suspend fun findBookInfoByBookId(bookAffiliationId: Long, affiliationId: Long): BookDetailInfoResult?
}