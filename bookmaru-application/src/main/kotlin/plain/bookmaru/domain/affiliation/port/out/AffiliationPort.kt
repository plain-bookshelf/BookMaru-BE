package plain.bookmaru.domain.affiliation.port.out

import plain.bookmaru.domain.affiliation.model.Affiliation

interface AffiliationPort {
    suspend fun findByAffiliationName(name: String): Affiliation?
    suspend fun findAll(): List<Affiliation>
    suspend fun findById(id: Long): Affiliation?
}