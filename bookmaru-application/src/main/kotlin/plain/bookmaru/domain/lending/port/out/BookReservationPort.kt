package plain.bookmaru.domain.lending.port.out

import plain.bookmaru.domain.lending.model.Reservation

interface BookReservationPort {
    suspend fun waiting(bookAffiliationId: Long): Int
    suspend fun findFirstReservationByBookAffiliationId(bookAffiliationId: Long): Reservation?
    suspend fun findReservation(bookAffiliationId: Long, memberId: Long): Reservation?

    fun save(reservation: Reservation)
    fun deleteReservation(memberId: Long, bookAffiliationId: Long)
}
