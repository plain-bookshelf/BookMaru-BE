package plain.bookmaru.domain.lending.persistent.entity.embedded

import jakarta.persistence.Embeddable

@Embeddable
data class BookRentalRecordEmbeddedId(
    val memberId: Long,
    val bookDetailId: Long
) {
}