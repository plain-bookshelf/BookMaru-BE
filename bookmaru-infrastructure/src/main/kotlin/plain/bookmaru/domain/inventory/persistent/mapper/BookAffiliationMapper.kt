package plain.bookmaru.domain.inventory.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.book.persistent.entity.BookEntity
import plain.bookmaru.domain.inventory.model.BookAffiliation
import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity

@Component
class BookAffiliationMapper {

    fun toDomain(entity: BookAffiliationEntity) : BookAffiliation {
        return BookAffiliation(
            id = entity.id,
            bookId = entity.bookEntity.id!!,
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
}