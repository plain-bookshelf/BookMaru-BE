package plain.bookmaru.domain.lending.port.out

import plain.bookmaru.domain.lending.model.Reservation

interface BookReservationPort {
    suspend fun waiting(bookAffiliationId: Long): Int
    suspend fun findFirstReservationByAffiliationId(affiliationId: Long): Reservation?

    fun save(reservation: Reservation)
    fun deleteReservation(memberId: Long, affiliationId: Long)
}