package plain.bookmaru.domain.affiliation.presentation.dto.response

import plain.bookmaru.domain.affiliation.port.out.result.AffiliationViewResult

data class AffiliationViewResponseDto(
    val affiliationViewResult: AffiliationViewResult
)