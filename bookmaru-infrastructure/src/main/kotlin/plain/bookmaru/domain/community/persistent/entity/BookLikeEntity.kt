package plain.bookmaru.domain.community.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(
    name = "book_like",
    indexes = [
        Index(name = "idx_book_id", columnList = "book_id"),
        Index(name = "idx_member_id", columnList = "member_id")
    ]
)
class BookLikeEntity(
    @EmbeddedId
    override val id: BookLikeEmbeddedId,
) : BaseEntity() {

    var status: Boolean = false
}