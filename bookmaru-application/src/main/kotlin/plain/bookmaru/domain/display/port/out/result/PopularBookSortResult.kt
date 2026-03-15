package plain.bookmaru.domain.display.port.out.result

data class PopularBookSortResult(
    val rank: Int,
    val id: Long,
    val bookImage: String,
    val title: String? = null,
    val author: String? = null,
    val genreList: List<BookGenreResult>? = null
)
