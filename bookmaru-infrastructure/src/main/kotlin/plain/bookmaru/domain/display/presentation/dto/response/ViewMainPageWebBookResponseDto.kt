package plain.bookmaru.domain.display.presentation.dto.response

import plain.bookmaru.domain.display.port.out.result.BookGenreResult
import plain.bookmaru.domain.display.port.out.result.BookSortResult

data class ViewMainPageWebBookResponseDto(
    val id: Long,
    val bookImage: String,
    val title: String,
    val author: String,
    val genreList: List<BookGenreResult>
) {
    companion object {
        fun from(result: List<BookSortResult>): List<ViewMainPageWebBookResponseDto> {
            return result.map {
                ViewMainPageWebBookResponseDto(
                    id = it.id,
                    bookImage = it.bookImage,
                    title = it.title ?: "",
                    author = it.author ?: "",
                    genreList = it.genreList ?: emptyList()
                )
            }
        }
    }
}