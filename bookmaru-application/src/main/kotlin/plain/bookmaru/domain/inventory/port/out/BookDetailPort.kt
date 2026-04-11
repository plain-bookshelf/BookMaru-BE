package plain.bookmaru.domain.inventory.port.out

import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.manager.port.out.resullt.RentalBookStatusCheckResult
import java.time.LocalDate

interface BookDetailPort {
    suspend fun findRentalBookDetailByBookAffiliationId(bookAffiliationId: Long) : BookDetail?
    suspend fun findRentalBookStatusCheckByAffiliationId(command: PageCommand, affiliationId: Long): PageResult<RentalBookStatusCheckResult>?
    suspend fun findRentalBookStatusCheckByAffiliationIdAndNickname(command: PageCommand, affiliationId: Long, nickname: String): PageResult<RentalBookStatusCheckResult>?
    suspend fun findRentalBookByBookDetailId(bookDetailId: Long): BookDetail?

    fun updateRental(rental: Rental, returnDate: LocalDate)
}