package plain.bookmaru.domain.affiliation.port.out.result

import plain.bookmaru.domain.affiliation.model.Affiliation

data class AffiliationViewResult(
    val affiliations: List<Affiliation>
)