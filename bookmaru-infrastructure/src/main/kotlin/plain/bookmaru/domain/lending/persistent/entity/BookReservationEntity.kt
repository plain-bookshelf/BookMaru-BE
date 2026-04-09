package plain.bookmaru.domain.lending.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookReservationEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(
    name = "book_reservation",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_book_affiliation_waiting_rank",
            columnNames = ["book_affiliation_id", "waiting_rank"]
        )
    ]
)
class BookReservationEntity(
    @EmbeddedId
    override val id: BookReservationEmbeddedId? = null,

    @Column(nullable = false)
    var waitingRank: Int,

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val memberEntity: BookReservationEntity
) : BaseEntity()