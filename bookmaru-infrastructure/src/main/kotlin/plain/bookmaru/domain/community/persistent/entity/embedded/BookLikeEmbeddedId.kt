package plain.bookmaru.domain.community.persistent.entity.embedded

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class BookLikeEmbeddedId(
    val memberId: Long,
    val bookAffiliationId: Long
) : Serializable