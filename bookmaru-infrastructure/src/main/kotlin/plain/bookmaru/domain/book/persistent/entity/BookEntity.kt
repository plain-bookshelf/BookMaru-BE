package plain.bookmaru.domain.book.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import plain.bookmaru.domain.community.persistent.entity.BookCommentEntity
import plain.bookmaru.domain.inventory.persistent.entity.BookDetailEntity
import plain.bookmaru.global.entity.BaseEntity

@Entity
@SequenceGenerator(
    name = "book_seq_generator",
    sequenceName = "book_seq",
    allocationSize = 100
)
class BookEntity(
    @Column(nullable = false, length = 100)
    val title: String,

    @Column(length = 100)
    val author: String,

    @Column(length = 20)
    val publicationDate: String,

    val bookImage : String,

    val publisher: String,

    val introduction: String,

    val similarityToken: String
) : BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq_generator")
    @Column(unique = true, nullable = false)
    override val id: Long? = null

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bookDetail", cascade = [CascadeType.ALL])
    val bookDetailEntities : MutableList<BookDetailEntity> = mutableListOf()

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val bookCommentEntities : MutableList<BookCommentEntity> = mutableListOf()

    var rentalCount: Int = 0

    var reservationCount: Int = 0

    var likeCount: Int = 0
}
