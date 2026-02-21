package plain.bookmaru.domain.verification.port.out

import plain.bookmaru.domain.affiliation.model.Affiliation
import plain.bookmaru.domain.verification.model.OfficialCode

interface OfficialCodePort {
    suspend fun save(officialCodes: List<OfficialCode>, affiliation: Affiliation)
    suspend fun load(code: String, affiliation: Affiliation): OfficialCode?
}