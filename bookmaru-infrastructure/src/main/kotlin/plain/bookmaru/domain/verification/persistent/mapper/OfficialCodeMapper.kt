package plain.bookmaru.domain.verification.persistent.mapper

import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.model.Affiliation
import plain.bookmaru.domain.affiliation.persistent.mapper.AffiliationMapper
import plain.bookmaru.domain.verification.persistent.entity.OfficialCodeEntity
import plain.bookmaru.domain.verification.model.OfficialCode

@Component
class OfficialCodeMapper(
    private val affiliationMapper: AffiliationMapper
) {
    fun toDomain(entity: OfficialCodeEntity) : OfficialCode {
        return OfficialCode(
            id = entity.id,
            affiliationId = entity.affiliation.id!!,
            role = entity.role,
            code = entity.code
        )
    }

    fun toEntity(domain: OfficialCode, affiliation: Affiliation) : OfficialCodeEntity {
        return OfficialCodeEntity(
            affiliation = affiliationMapper.toEntity(affiliation),
            role = domain.role,
            code = domain.code
        )
    }

    fun toEntityList(domains: List<OfficialCode>, affiliation: Affiliation) : List<OfficialCodeEntity>
        = domains.map { toEntity(it, affiliation) }
}