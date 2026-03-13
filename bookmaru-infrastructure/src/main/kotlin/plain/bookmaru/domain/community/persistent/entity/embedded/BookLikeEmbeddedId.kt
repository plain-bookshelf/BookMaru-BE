package plain.bookmaru.domain.community.persistent.entity.embedded

import jakarta.persistence.Embeddable

@Embeddable
data class BookLikeEmbeddedId(
    val memberId: Long,
    val bookId: Long
)