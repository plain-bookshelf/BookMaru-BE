package plain.bookmaru.domain.display.port.out.result

import kotlinx.serialization.Serializable

@Serializable
data class BookSortResult(
    val id: Long,
    val bookImage: String,
    val title: String? = null,
    val author: String? = null,
    val genreList: List<BookGenreResult>? = null
)