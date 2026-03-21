package plain.bookmaru.domain.lending.persistent.entity.embedded

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class BookReservationEmbeddedId(
    val memberId: Long,
    val bookAffiliationId: Long
) : Serializable