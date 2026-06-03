package plain.bookmaru.domain.lending.persistent.entity.embedded

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class BookRentalRecordEmbeddedId(
    val memberId: Long,
    val bookDetailId: Long
) : Serializable