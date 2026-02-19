package plain.bookmaru.domain.officialkey.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.affiliation.vo.Affiliation
import plain.bookmaru.domain.auth.vo.Authority

@Aggregate
data class OfficialVerification(
    val affiliation: Affiliation,
    val authority: Authority,
    val value: String
)