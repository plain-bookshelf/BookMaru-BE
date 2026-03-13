package plain.bookmaru.domain.inventory.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.global.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@SequenceGenerator(
    name = "bookDetail_seq_generator",
    sequenceName = "bookDetail_seq",
    allocationSize = 200
)
class BookDetailEntity(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val bookEntity: BookEntity,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id", nullable = false)
    val affiliationEntity: AffiliationEntity,

    @Column(nullable = false)
    val rentalRequestStatus : Boolean = false,

    @Column(nullable = false)
    val rentalStatus : Boolean = false,

    @Column(nullable = true)
    val returnDate : LocalDateTime? = null,

    @Column(nullable = false)
    val registrationNumber : String,

    @Column(nullable = false)
    val callNumber : String,
) : BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookDetail_seq_generator")
    @Column(nullable = false, unique = true)
    override val id: Long? = null
}