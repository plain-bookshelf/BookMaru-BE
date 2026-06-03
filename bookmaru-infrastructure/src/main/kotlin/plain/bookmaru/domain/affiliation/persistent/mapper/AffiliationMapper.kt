package plain.bookmaru.domain.affiliation.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.affiliation.model.Affiliation

@Component
class AffiliationMapper {

    fun toDomain(entity: AffiliationEntity?) : Affiliation {
        return Affiliation(
            id = entity?.id,
            affiliationName = entity?.affiliationName ?: ""
        )
    }

    fun toEntity(domain: Affiliation) : AffiliationEntity {
        return AffiliationEntity(
            id = domain.id,
            affiliationName = domain.affiliationName
        )
    }
}