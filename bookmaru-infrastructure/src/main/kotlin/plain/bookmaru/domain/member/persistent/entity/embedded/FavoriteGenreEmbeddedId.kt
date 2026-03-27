package plain.bookmaru.domain.member.persistent.entity.embedded

import jakarta.persistence.Embeddable

@Embeddable
data class FavoriteGenreEmbeddedId(
    val memberId: Long,
    val genreId: Long
)
