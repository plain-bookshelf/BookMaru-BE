package plain.bookmaru.domain.community.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import plain.bookmaru.domain.community.persistent.entity.embedded.BookCommentLikeEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(
    name = "book_comment_like",
    indexes = [
        Index(name = "idx_book_comment_id", columnList = "book_comment_id"),
        Index(name = "idx_member_id", columnList = "member_id")
    ]
)
class BookCommentLikeEntity(
    @EmbeddedId
    override val id: BookCommentLikeEmbeddedId
) : BaseEntity() {

    var status: Boolean = false
}