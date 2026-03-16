package plain.bookmaru.domain.community.persistent.entity

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
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.global.entity.BaseEntity

@Entity
@SequenceGenerator(
    name = "bookComment_seq_generator",
    sequenceName = "bookComment_seq",
    allocationSize = 50
)
@Table(
    name = "book_comment",
    indexes = [
        Index(name = "idx_book_id", columnList = "book_id"),
        Index(name = "idx_member_id", columnList = "member_id")
    ]
)
class BookCommentEntity(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val memberEntity: MemberEntity,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val bookEntity: BookEntity
) : BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookComment_seq_generator")
    @Column(nullable = false, unique = true)
    override val id: Long? = null

    @Column(nullable = false)
    var comment: String = "undefined"

    @Column(nullable = false)
    var likeCount: Int = 0

    @Column(nullable = false)
    var starCount: Int = 0
}