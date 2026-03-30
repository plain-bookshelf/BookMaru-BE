package plain.bookmaru.domain.inventory.port.out

import plain.bookmaru.domain.inventory.model.BookDetail
import plain.bookmaru.domain.lending.model.Rental

interface BookDetailPort {
    suspend fun findRentalBookDetailByBookAffiliationId(bookAffiliationId: Long) : BookDetail?

    fun updateRental(rental: Rental)
}