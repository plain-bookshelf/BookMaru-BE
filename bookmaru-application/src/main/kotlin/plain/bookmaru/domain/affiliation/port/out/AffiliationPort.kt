package plain.bookmaru.domain.affiliation.port.out

import plain.bookmaru.domain.affiliation.vo.Affiliation

interface AffiliationPort {
    suspend fun findByAffiliationName(name: String): Affiliation
    suspend fun findAll(): List<Affiliation>
}