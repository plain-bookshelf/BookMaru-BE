package plain.bookmaru.domain.affiliation.port.`in`

import plain.bookmaru.domain.affiliation.result.AffiliationViewResult

interface AffiliationViewUseCase {
    suspend fun execute() : AffiliationViewResult
}