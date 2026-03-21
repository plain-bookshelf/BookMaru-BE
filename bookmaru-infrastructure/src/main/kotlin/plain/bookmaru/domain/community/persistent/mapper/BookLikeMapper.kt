package plain.bookmaru.domain.community.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.community.model.BookLike
import plain.bookmaru.domain.community.persistent.entity.BookLikeEntity
import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId

@Component
class BookLikeMapper {

    fun toDomain(entity: BookLikeEntity) : BookLike {
        return BookLike(
            memberId = entity.id.memberId,
            bookAffiliationId = entity.id.bookAffiliationId,
            status = entity.status
        )
    }

    fun toEntity(domain: BookLike) : BookLikeEntity {
        val embeddedId = BookLikeEmbeddedId(
            memberId = domain.memberId,
            bookAffiliationId = domain.bookAffiliationId
        )

        return BookLikeEntity(
            id = embeddedId,
        ).apply {
            status = domain.status
        }
    }
}