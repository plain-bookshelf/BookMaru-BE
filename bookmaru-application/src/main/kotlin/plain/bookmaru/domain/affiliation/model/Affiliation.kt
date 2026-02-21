package plain.bookmaru.domain.affiliation.model

import plain.bookmaru.common.annotation.Aggregate

@Aggregate
class Affiliation(
    val id: Long? = null,
    val affiliationName: String
) {
    init {
        require(affiliationName.isNotBlank()) { "소속 정보가 필요합니다."}
    }
}
