package plain.bookmaru.domain.display.port.`in`.command

data class ViewBookDetailPageCommand(
    val bookAffiliationId: Long,
    val affiliationId: Long,
    val memberId: Long
)
