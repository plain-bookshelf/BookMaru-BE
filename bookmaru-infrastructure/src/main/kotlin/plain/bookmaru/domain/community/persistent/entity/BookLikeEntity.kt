package plain.bookmaru.domain.community.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
class BookLikeEntity(
    @EmbeddedId
    override val id: BookLikeEmbeddedId,
) : BaseEntity() {

    var status: Boolean = false
}