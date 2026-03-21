package plain.bookmaru.domain.book.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.global.entity.BaseEntity

@Entity
@SequenceGenerator(
    name = "book_seq_generator",
    sequenceName = "book_seq",
    allocationSize = 100
)
@Table(name = "book")
class BookEntity(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id", nullable = false)
    val affiliationEntity: AffiliationEntity,

    @Column(nullable = false, length = 100)
    val title: String,

    @Column(length = 100)
    val author: String,

    @Column(length = 20)
    val publicationDate: String,

    val bookImage : String,

    val publisher: String,

    val introduction: String
) : BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq_generator")
    @Column(unique = true, nullable = false)
    override val id: Long? = null
}
