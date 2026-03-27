package plain.bookmaru.domain.member.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import plain.bookmaru.domain.member.persistent.entity.embedded.FavoriteGenreEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
class FavoriteGenre(
    @EmbeddedId
    override val id: FavoriteGenreEmbeddedId
) : BaseEntity()