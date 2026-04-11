package plain.bookmaru.domain.lending.port.out

import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.manager.port.out.result.RentalRequestCheckResult

interface BookRentalRecordPort {
    fun save(renter: Rental)
    suspend fun findRentalRequestBookByAffiliationId(affiliationId: Long) : List<RentalRequestCheckResult>?
}