package plain.bookmaru.domain.display.presentation.dto.response

import plain.bookmaru.domain.display.port.out.result.BookSortResult

data class ViewMainPageAppBookResponseDto (
    val id: Long,
    val bookImage: String
) {
    companion object {
        fun from(result: List<BookSortResult>): List<ViewMainPageAppBookResponseDto> {
            return result.map {
                ViewMainPageAppBookResponseDto(
                    id = it.id,
                    bookImage = it.bookImage
                )
            }
        }
    }
}