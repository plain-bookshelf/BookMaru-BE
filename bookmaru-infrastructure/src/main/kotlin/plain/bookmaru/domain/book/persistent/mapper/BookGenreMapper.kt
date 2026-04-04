package plain.bookmaru.domain.book.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.book.model.Book
import plain.bookmaru.domain.book.model.BookGenre
import plain.bookmaru.domain.book.persistent.entity.BookGenreEntity
import plain.bookmaru.domain.book.persistent.entity.embedded.BookGenreEmbeddedId

@Component
class BookGenreMapper(
    private val genreMapper: GenreMapper,
    private val bookMapper: BookMapper
) {

    fun toDomain(entity: BookGenreEntity): BookGenre {
        return BookGenre(
            bookId = entity.bookEntity.id!!,
            genre = genreMapper.toDomain(entity.genreEntity)
        )
    }

    fun toEntity(domain: BookGenre, book: Book): BookGenreEntity {
        val embeddedId = BookGenreEmbeddedId(
            bookId = domain.bookId,
            genreId = domain.genre.id
        )

        return BookGenreEntity(
            id = embeddedId,
            genreEntity = genreMapper.toEntity(domain.genre),
            bookEntity = bookMapper.toEntity(book)
        )
    }

    fun toDomainList(entities: List<BookGenreEntity>): List<BookGenre>
    = entities.map { toDomain(it) }

    fun toEntityList(domains: List<BookGenre>, book: Book): List<BookGenreEntity>
    = domains.map { toEntity(
        it,
        book = book
    ) }
}