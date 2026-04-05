package plain.bookmaru.domain.book.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.book.model.BookGenre
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.book.persistent.entity.BookGenreEntity
import plain.bookmaru.domain.book.persistent.entity.embedded.BookGenreEmbeddedId

@Component
class BookGenreMapper(
    private val genreMapper: GenreMapper
) {

    fun toDomain(entity: BookGenreEntity): BookGenre {
        return BookGenre(
            bookId = entity.bookEntity.id!!,
            genre = genreMapper.toDomain(entity.genreEntity)
        )
    }

    fun toEntity(domain: BookGenre, bookEntity: BookEntity): BookGenreEntity {
        val embeddedId = BookGenreEmbeddedId(
            bookId = domain.bookId,
            genreId = domain.genre.id
        )

        return BookGenreEntity(
            id = embeddedId,
            genreEntity = genreMapper.toEntity(domain.genre),
            bookEntity = bookEntity
        )
    }

    fun toDomainList(entities: List<BookGenreEntity>): List<BookGenre>
    = entities.map { toDomain(it) }

    fun toEntityList(domains: List<BookGenre>, bookEntity: BookEntity): List<BookGenreEntity>
    = domains.map { toEntity(
        it,
        bookEntity = bookEntity
    ) }
}