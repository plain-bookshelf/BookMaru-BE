package plain.bookmaru.domain.lending.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.domain.lending.vo.BookRecord

@Aggregate
class Rental(
    val memberId: Long? = null,
    val bookDetailId: Long,
    val rentalRequestStatus: Boolean,
    val rentalStatus: RentalStatus = RentalStatus.RETURN,
    val bookRecord: BookRecord? = null
)