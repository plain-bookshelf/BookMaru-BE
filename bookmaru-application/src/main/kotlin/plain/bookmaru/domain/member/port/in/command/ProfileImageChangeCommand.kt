package plain.bookmaru.domain.member.port.`in`.command

data class ProfileImageChangeCommand(
    val username: String,
    val newProfileImageUrl: String
) {
}