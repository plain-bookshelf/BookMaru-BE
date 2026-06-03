package plain.bookmaru.domain.book.persistent.entity.embedded

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class BookGenreEmbeddedId(
    val bookId: Long,
    val genreId: Long
) : Serializable