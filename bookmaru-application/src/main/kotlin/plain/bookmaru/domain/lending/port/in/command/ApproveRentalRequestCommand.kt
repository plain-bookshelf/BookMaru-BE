package plain.bookmaru.domain.lending.port.`in`.command

data class ApproveRentalRequestCommand(
    val bookDetailId: Long,
    val affiliationId: Long
)
