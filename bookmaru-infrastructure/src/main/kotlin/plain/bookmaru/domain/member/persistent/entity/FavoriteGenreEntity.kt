package plain.bookmaru.domain.member.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import plain.bookmaru.domain.member.persistent.entity.embedded.FavoriteGenreEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(name = "favorite_genre")
class FavoriteGenreEntity(
    @EmbeddedId
    override val id: FavoriteGenreEmbeddedId
) : BaseEntity()
