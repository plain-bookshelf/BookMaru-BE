package plain.bookmaru.domain.verification.port.`in`.command

data class OfficialCodeCommand(
    val affiliationName: String
) {
    init {
        require(affiliationName.isNotBlank())
        require(affiliationName.length < 20)
    }
}