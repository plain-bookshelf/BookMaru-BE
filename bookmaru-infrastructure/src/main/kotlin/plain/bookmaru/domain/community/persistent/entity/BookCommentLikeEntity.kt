package plain.bookmaru.domain.community.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import plain.bookmaru.domain.community.persistent.entity.embedded.BookCommentLikeEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
class BookCommentLikeEntity(
    @EmbeddedId
    override val id: BookCommentLikeEmbeddedId
) : BaseEntity() {

    var status: Boolean = false
}