package plain.bookmaru.domain.inventory.port.out.result

import plain.bookmaru.domain.inventory.model.BookAffiliation

data class BookDetailInfoResult(
    val bookAffiliation: BookAffiliation,
    val affiliationName: String,
    val availableCount: Int,
    val isBookLiked: Boolean
)
