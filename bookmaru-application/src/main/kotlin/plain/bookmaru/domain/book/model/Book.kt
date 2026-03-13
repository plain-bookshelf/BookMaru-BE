package plain.bookmaru.domain.book.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.book.vo.BookInfo

@Aggregate
class Book(
    val id: Long? = null,
    val bookInfo: BookInfo,
    val rentalCount: Int,
    val reservationCount: Int,
    val likeCount: Int,
    val similarityToken: String
)