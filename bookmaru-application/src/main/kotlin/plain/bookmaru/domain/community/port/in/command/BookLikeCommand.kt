package plain.bookmaru.domain.community.port.`in`.command

data class BookLikeCommand(
    val bookAffiliationId : Long,
    val memberId : Long
)
