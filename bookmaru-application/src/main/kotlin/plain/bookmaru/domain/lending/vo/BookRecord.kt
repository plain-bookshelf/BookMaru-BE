package plain.bookmaru.domain.lending.vo

import java.time.LocalDate
import java.time.LocalDateTime

data class BookRecord(
    val rentalDate: LocalDateTime,
    val returnDate: LocalDate? = null
)