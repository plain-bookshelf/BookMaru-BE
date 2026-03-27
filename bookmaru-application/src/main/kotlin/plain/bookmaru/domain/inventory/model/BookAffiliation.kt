package plain.bookmaru.domain.inventory.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.book.model.Book

@Aggregate
class BookAffiliation(
    val id: Long? = null,
    val book: Book,
    val affiliationId: Long,
    val rentalCount: Int,
    val reservationCount: Int,
    likeCount: Int,
    val similarityToken: String
) {
    var likeCount: Int = likeCount
        private set

    fun modifyLikeCount() {
        this.likeCount++
    }
}