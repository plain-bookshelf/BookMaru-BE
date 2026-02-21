package plain.bookmaru.domain.affiliation.result

import plain.bookmaru.domain.affiliation.model.Affiliation

data class AffiliationViewResult(
    val affiliations: List<Affiliation>
)
