package plain.bookmaru.domain.inventory.model

import plain.bookmaru.common.annotation.Aggregate

@Aggregate
class BookAffiliation(
    val id: Long? = null,
    val bookId: Long,
    val affiliationId: Long,
    val rentalCount: Int,
    val reservationCount: Int,
    val likeCount: Int,
    val similarityToken: String
)