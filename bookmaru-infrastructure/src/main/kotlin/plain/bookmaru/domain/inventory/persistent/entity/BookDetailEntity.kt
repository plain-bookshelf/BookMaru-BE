package plain.bookmaru.domain.inventory.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import plain.bookmaru.domain.inventory.vo.RentalStatus
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.global.entity.BaseEntity
import java.time.LocalDate

@Entity
@SequenceGenerator(
    name = "bookDetail_seq_generator",
    sequenceName = "bookDetail_seq",
    allocationSize = 200
)
@Table(
    name = "book_detail",
    indexes = [
        Index(name = "idx_book_detail_book_affiliation_id", columnList = "book_affiliation_id"),
        Index(name = "idx_book_detail_member_id", columnList = "member_id")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_id_member_id",
            columnNames = ["id", "member_id"]
        )
    ]
)
class BookDetailEntity(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_affiliation_id", nullable = false)
    val bookAffiliationEntity: BookAffiliationEntity,

    @Column(nullable = false)
    val registrationNumber : String,

    @Column(nullable = false)
    val callNumber : String,
) : BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookDetail_seq_generator")
    @Column(nullable = false, unique = true)
    override val id: Long? = null

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var memberEntity: MemberEntity? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var rentalStatus : RentalStatus = RentalStatus.RETURN

    @Column(nullable = true)
    var returnDate : LocalDate? = null
}