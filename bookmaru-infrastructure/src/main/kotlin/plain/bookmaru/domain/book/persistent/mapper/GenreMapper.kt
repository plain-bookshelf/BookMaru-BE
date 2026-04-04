package plain.bookmaru.domain.book.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.book.model.Genre
import plain.bookmaru.domain.book.persistent.entity.GenreEntity

@Component
class GenreMapper {

    fun toDomain(entity: GenreEntity) : Genre {
        return Genre(
            id = entity.id!!,
            genreName = entity.genreName
        )
    }

    fun toEntity(domain: Genre): GenreEntity {
        return GenreEntity(
            id = domain.id,
            genreName = domain.genreName
        )
    }

    fun toDomainList(entities: List<GenreEntity>): List<Genre>
        = entities.map { toDomain(it) }

    fun toEntityList(domains: List<Genre>): List<GenreEntity>
        = domains.map { toEntity(it) }
}
