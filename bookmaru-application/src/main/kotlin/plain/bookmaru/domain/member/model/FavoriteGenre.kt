package plain.bookmaru.domain.member.model

import plain.bookmaru.common.annotation.Aggregate

@Aggregate
class FavoriteGenre(
    val genreId: Long,
    val memberId: Long,
)