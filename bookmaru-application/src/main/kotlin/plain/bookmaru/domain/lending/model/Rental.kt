package plain.bookmaru.domain.lending.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.inventory.vo.RentalStatus
import java.time.LocalDate

@Aggregate
class Rental(
    val memberId: Long? = null,
    val bookDetailId: Long,
    val rentalStatus: RentalStatus = RentalStatus.RETURN,
    val returnDate: LocalDate
)