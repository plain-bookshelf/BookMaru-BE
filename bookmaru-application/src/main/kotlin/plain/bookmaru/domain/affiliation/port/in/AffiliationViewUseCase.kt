package plain.bookmaru.domain.affiliation.port.`in`

import plain.bookmaru.domain.affiliation.port.out.result.AffiliationViewResult

interface AffiliationViewUseCase {
    suspend fun execute() : AffiliationViewResult
}