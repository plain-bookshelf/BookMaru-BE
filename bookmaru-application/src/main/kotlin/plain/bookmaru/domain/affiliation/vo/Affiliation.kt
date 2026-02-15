package plain.bookmaru.domain.affiliation.vo

data class Affiliation(
    val id: Long? = null,
    val affiliation: String
) {
    init {
        require(affiliation.isNotBlank()) { "소속 정보가 필요합니다."}
    }
}
