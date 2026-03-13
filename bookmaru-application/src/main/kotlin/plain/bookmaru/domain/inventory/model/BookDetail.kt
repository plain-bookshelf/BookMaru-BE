package plain.bookmaru.domain.inventory.model

import plain.bookmaru.domain.inventory.vo.BookDetailDiscernment
import plain.bookmaru.domain.lending.model.Rental

class BookDetail(
    val id: Long? = null,
    val bookId: Long,
    val rental: Rental,
    val bookDetailDiscernment: BookDetailDiscernment
)