package plain.bookmaru.domain.display.port.out.result

import plain.bookmaru.domain.book.vo.BookInfo

data class BookDetailPageResult(
    val affiliationName: String,
    val bookInfo: BookInfo,
    val isEnableRental: Boolean,
    val isLiked: Boolean
)