package plain.bookmaru.domain.affiliation.service

import plain.bookmaru.common.annotation.ReadOnlyService
import plain.bookmaru.domain.affiliation.port.`in`.AffiliationViewUseCase
import plain.bookmaru.domain.affiliation.port.out.AffiliationPort
import plain.bookmaru.domain.affiliation.result.AffiliationViewResult

@ReadOnlyService
class AffiliationViewService(
    private val affiliationPort: AffiliationPort
) : AffiliationViewUseCase {

    override suspend fun view(): AffiliationViewResult
    = AffiliationViewResult(
        affiliationPort.findAll()
    )
}