package plain.bookmaru.domain.lending.port.out

import plain.bookmaru.domain.lending.model.Rental

interface BookRentalRecordPort {
    suspend fun save(renter: Rental)
}