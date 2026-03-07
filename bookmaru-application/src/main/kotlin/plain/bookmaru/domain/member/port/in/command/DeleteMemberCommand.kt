package plain.bookmaru.domain.member.port.`in`.command

data class DeleteMemberCommand(
    val username: String,
    val accessToken: String
)