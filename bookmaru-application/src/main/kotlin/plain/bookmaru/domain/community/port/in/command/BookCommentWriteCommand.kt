package plain.bookmaru.domain.community.port.`in`.command

data class BookCommentWriteCommand(
    val memberId: Long,
    val bookAffiliationId: Long,
    val comment: String
)