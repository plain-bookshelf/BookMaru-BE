package plain.bookmaru.domain.lending.port.out

import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.port.out.result.RentalRequestCheckResult

interface BookRentalRecordPort {
    fun save(renter: Rental)
    suspend fun update(bookDetail: BookDetail)
    suspend fun findRentalRequestBookByAffiliationId(affiliationId: Long) : List<RentalRequestCheckResult>?
}