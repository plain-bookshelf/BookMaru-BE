package plain.bookmaru.domain.lending.port.out

import plain.bookmaru.domain.lending.model.Reservation

interface BookReservationPort {
    suspend fun waiting(bookAffiliationId: Long): Int
    suspend fun save(reservation: Reservation)
}