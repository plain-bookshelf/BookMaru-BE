package plain.bookmaru.domain.display.port.out.result

data class RecentBookSortResult(
    val id: Long,
    val bookImage: String,
    val title: String? = null,
    val author: String? = null,
    val genreList: List<BookGenreResult>? = null
)
