package plain.bookmaru.domain.affiliation.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity

interface AffiliationRepository : JpaRepository<AffiliationEntity, Long> {
    fun findByAffiliationName(affiliationName: String):AffiliationEntity?
    override fun getReferenceById(id: Long) : AffiliationEntity
}