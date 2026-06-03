package plain.bookmaru.domain.inventory.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.global.entity.BaseEntity

@Entity
@SequenceGenerator(
    name = "book_affiliation_seq_generator",
    sequenceName = "book_affiliation_seq",
    allocationSize = 100
)
@Table(
    name = "book_affiliation",
    indexes = [
        Index(name = "idx_book_affiliation_book_id", columnList = "book_id"),
        Index(name = "idx_book_affiliation_affiliation_id", columnList = "affiliation_id"),
        Index(name = "idx_book_affiliation_affiliation_book", columnList = "affiliation_id, book_id"),
        Index(name = "idx_book_affiliation_similarity", columnList = "similarity_token")
    ]
)
class BookAffiliationEntity(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val bookEntity: BookEntity,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id", nullable = false)
    val affiliationEntity: AffiliationEntity,

    @Column(name = "similarity_token", columnDefinition = "tsvector", insertable = false, updatable = false)
    val similarityToken: String?

) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_affiliation_seq_generator")
    @Column(nullable = false, unique = true)
    override val id: Long? = null

    var rentalCount: Int = 0

    var reservationCount: Int = 0

    var likeCount: Int = 0
}
