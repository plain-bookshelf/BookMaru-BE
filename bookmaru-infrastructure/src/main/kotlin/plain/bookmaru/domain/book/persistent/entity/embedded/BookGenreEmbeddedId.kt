package plain.bookmaru.domain.book.persistent.entity.embedded

import jakarta.persistence.Embeddable

@Embeddable
class BookGenreEmbeddedId(
    val bookId: Long,
    val genreId: Long
)