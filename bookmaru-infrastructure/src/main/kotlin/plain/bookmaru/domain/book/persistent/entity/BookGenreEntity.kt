package plain.bookmaru.domain.book.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import plain.bookmaru.domain.book.persistent.entity.embedded.BookGenreEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
class BookGenreEntity(
    @EmbeddedId
    override val id: BookGenreEmbeddedId
) : BaseEntity()