package plain.bookmaru.domain.book.persistent.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import plain.bookmaru.domain.book.persistent.entity.embedded.BookGenreEmbeddedId
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(
    name = "book_genre",
    indexes = [Index(name = "idx_book_id", columnList = "book_id")]
)
class BookGenreEntity(
    @EmbeddedId
    override val id: BookGenreEmbeddedId,

    @MapsId("genreId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    val genreEntity: GenreEntity,

    @MapsId("bookId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    val bookEntity: BookEntity
) : BaseEntity()