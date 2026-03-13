package plain.bookmaru.domain.community.persistent.entity.embedded

import jakarta.persistence.Embeddable

@Embeddable
data class BookCommentLikeEmbeddedId(
    val memberId: Long,
    val bookCommentId: Long
)
