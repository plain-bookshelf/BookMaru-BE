package plain.bookmaru.domain.inventory.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.book.persistent.mapper.BookMapper
import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity

@Component
class BookAffiliationMapper(
    private val bookMapper: BookMapper
) {

    fun toDomain(entity: BookAffiliationEntity) : BookAffiliation {
        return BookAffiliation(
            id = entity.id,
            book = bookMapper.toDomain(entity.bookEntity),
            affiliationId = entity.affiliationEntity.id!!,
            rentalCount = entity.rentalCount,
            reservationCount = entity.reservationCount,
            likeCount = entity.likeCount,
            similarityToken = entity.similarityToken,
        )
    }

    fun toEntity(domain: BookAffiliation, affiliationEntity: AffiliationEntity, bookEntity: BookEntity) : BookAffiliationEntity {
        return BookAffiliationEntity(
            bookEntity = bookEntity,
            affiliationEntity = affiliationEntity,
            similarityToken = domain.similarityToken
        ).apply {
            this.rentalCount = domain.rentalCount
            this.reservationCount = domain.reservationCount
            this.likeCount = domain.likeCount
        }
    }

    fun updateEntity(entity: BookAffiliationEntity, domain: BookAffiliation) {
        entity.rentalCount = domain.rentalCount
        entity.reservationCount = domain.reservationCount
        entity.likeCount = domain.likeCount
    }

    fun toDomainList(entities: List<BookAffiliationEntity>) : List<BookAffiliation> {
        return entities.map { toDomain(it) }
    }
}