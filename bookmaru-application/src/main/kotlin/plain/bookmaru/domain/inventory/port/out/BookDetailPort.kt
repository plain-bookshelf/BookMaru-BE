package plain.bookmaru.domain.inventory.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.inventory.port.out.result.BookNotificationInfo
import plain.bookmaru.domain.manager.port.out.result.RentalBookStatusCheckResult

interface BookDetailPort {
    suspend fun findRentalBookDetailByBookAffiliationId(bookAffiliationId: Long) : BookDetail?
    suspend fun findRentalRequestBookDetailById(bookDetailId: Long, affiliationId: Long): BookDetail?
    suspend fun findRentalBookStatusCheckByAffiliationId(command: PageCommand, affiliationId: Long): PageResult<RentalBookStatusCheckResult>?
    suspend fun findRentalBookStatusCheckByAffiliationIdAndNickname(command: PageCommand, affiliationId: Long, nickname: String): PageResult<RentalBookStatusCheckResult>?
    suspend fun findRentalBookByBookDetailId(bookDetailId: Long): BookDetail?
    suspend fun findBookNotificationInfoByBookDetailId(bookDetailId: Long): BookNotificationInfo?

    fun updateRental(bookDetail: BookDetail): Long
    fun approveRentalRequest(bookDetail: BookDetail): Long
    fun assignReturnedRental(bookDetail: BookDetail): Long
}
