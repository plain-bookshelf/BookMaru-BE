package plain.bookmaru.domain.lending.port.`in`.command

data class ReturnCommand(
    val bookDetailId: Long,
    val affiliationId: Long
)
