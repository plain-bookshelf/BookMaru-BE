package plain.bookmaru.domain.display.port.out.result

data class BookInfoResult(
    val title: String,
    val author: String,
    val publicationDate: String,
    val introduction: String,
    val bookImage: String,
    val publisher: String,
    val likeCount: Int
)
