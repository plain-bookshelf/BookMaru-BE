package plain.bookmaru.domain.lending.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import plain.bookmaru.domain.inventory.persistent.entity.BookDetailEntity
import plain.bookmaru.domain.lending.persistent.entity.embedded.BookRentalRecordEmbeddedId
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.global.entity.BaseEntity
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "book_rental_record",
    indexes = [
        Index(name = "idx_book_rental_record_detail_return", columnList = "book_detail_id, return_date"),
        Index(name = "idx_book_rental_record_member", columnList = "member_id")
    ]
)
class BookRentalRecordEntity(
    @EmbeddedId
    override val id: BookRentalRecordEmbeddedId,

    val rentalDate: LocalDateTime,

    var returnDate: LocalDate? = null,

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val memberEntity: MemberEntity,

    @MapsId("bookDetailId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_detail_id")
    val bookDetailEntity: BookDetailEntity
) : BaseEntity()
