package plain.bookmaru.domain.book.vo

data class BookInfo(
    val affiliationName: String,
    val title: String,
    val author: String,
    val publicationDate: String,
    val introduction: String,
    val bookImage: String,
    val publisher: String
)