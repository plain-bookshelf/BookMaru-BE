package plain.bookmaru.domain.book.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.book.model.Genre
import plain.bookmaru.domain.book.persistent.entity.QBookGenreEntity
import plain.bookmaru.domain.book.persistent.entity.QGenreEntity
import plain.bookmaru.domain.book.persistent.mapper.GenreMapper
import plain.bookmaru.domain.book.port.out.BookGenrePort
import plain.bookmaru.global.config.DbProtection

@Component
class BookGenrePersistenceAdapter(
    private val queryFactory: JPAQueryFactory,
    private val genreMapper: GenreMapper,
    private val dbProtection: DbProtection
) : BookGenrePort {
    private val bookGenre = QBookGenreEntity.bookGenreEntity
    private val genreEntity = QGenreEntity.genreEntity

    override suspend fun loadBookGenre(ids: List<Long>): Map<Long, List<Genre>> = dbProtection.withReadOnly{
        val bookGenres = queryFactory
            .selectFrom(bookGenre)
            .join(bookGenre.genreEntity, genreEntity).fetchJoin()
            .where(bookGenre.id.bookId.`in`(ids))
            .fetch()

        return@withReadOnly bookGenres.groupBy(
            keySelector = { it.id.bookId },
            valueTransform = { genreMapper.toDomain(it.genreEntity) }
        )
    }
}