package plain.bookmaru.domain.verification.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.verification.persistent.entity.OfficialCodeEntity

interface OfficialCodeRepository : JpaRepository<OfficialCodeEntity, Long> {
    fun findByAffiliationAndCode(affiliationEntity: AffiliationEntity, code: String): OfficialCodeEntity?
}