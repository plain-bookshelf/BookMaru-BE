package plain.bookmaru.domain.affiliation.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.affiliation.vo.Affiliation

@Component
class AffiliationMapper {

    fun toDomain(entity: AffiliationEntity?) : Affiliation {
        return Affiliation(
            affiliation = entity?.affiliationName ?: ""
        )
    }

    fun toEntity(domain: Affiliation) : AffiliationEntity {
        return AffiliationEntity(
            affiliationName = domain.affiliation
        )
    }
}