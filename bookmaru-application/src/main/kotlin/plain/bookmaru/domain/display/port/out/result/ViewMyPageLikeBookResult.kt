package plain.bookmaru.domain.display.port.out.result

data class ViewMyPageLikeBookResult(
    val bookAffiliationId: Long,
    val title: String,
    val bookImage: String
)
