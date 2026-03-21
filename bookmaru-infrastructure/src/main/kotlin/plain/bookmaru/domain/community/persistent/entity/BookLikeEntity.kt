package plain.bookmaru.domain.community.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(name = "book_like")
class BookLikeEntity(
    @EmbeddedId
    override val id: BookLikeEmbeddedId,
) : BaseEntity() {

    var status: Boolean = false
}