package plain.bookmaru.domain.inventory.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.inventory.vo.BookDetailDiscernment

@Aggregate
class BookDetail(
    val id: Long? = null,
    val bookId: Long,
    val affiliationId: Long,
    val bookDetailDiscernment: BookDetailDiscernment
)