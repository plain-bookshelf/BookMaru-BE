package plain.bookmaru.domain.lending.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.lending.vo.BookRecord

@Aggregate
class Rental(
    val memberId: Long,
    val bookDetailId: Long,
    val bookRecord: BookRecord
)