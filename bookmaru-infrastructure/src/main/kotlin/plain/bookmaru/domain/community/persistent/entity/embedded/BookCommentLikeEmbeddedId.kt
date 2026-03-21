package plain.bookmaru.domain.community.persistent.entity.embedded

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class BookCommentLikeEmbeddedId(
    val memberId: Long,
    val bookCommentId: Long
) : Serializable