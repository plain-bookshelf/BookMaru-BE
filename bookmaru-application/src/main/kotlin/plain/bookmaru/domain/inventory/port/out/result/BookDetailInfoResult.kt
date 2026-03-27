package plain.bookmaru.domain.inventory.port.out.result

import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.inventory.model.BookAffiliation

data class BookDetailInfoResult(
    val book: Book,
    val bookAffiliation: BookAffiliation,
    val affiliationName: String,
    val availableCount: Int,
    val isBookLiked: Boolean
)
