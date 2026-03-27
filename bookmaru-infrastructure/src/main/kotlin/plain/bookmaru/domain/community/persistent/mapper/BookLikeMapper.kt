package plain.bookmaru.domain.community.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.community.model.BookLike
import plain.bookmaru.domain.community.persistent.entity.BookLikeEntity
import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId
import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

@Component
class BookLikeMapper {

    fun toDomain(entity: BookLikeEntity) : BookLike {
        return BookLike(
            memberId = entity.id.memberId,
            bookAffiliationId = entity.id.bookAffiliationId
        )
    }

    fun toEntity(domain: BookLike) : BookLikeEntity {
        val embeddedId = BookLikeEmbeddedId(
            memberId = domain.memberId,
            bookAffiliationId = domain.bookAffiliationId
        )

        return BookLikeEntity(
            id = embeddedId
        )
    }
}