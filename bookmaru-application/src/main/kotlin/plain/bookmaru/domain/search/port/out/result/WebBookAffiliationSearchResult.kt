package plain.bookmaru.domain.search.port.out.result

data class WebBookAffiliationSearchResult(
    val bookAffiliationId: Long,
    val title: String,
    val author: String,
    val introduction: String,
    val publisher: String,
    val publicationDate: String,
    val bookImage: String,
)
