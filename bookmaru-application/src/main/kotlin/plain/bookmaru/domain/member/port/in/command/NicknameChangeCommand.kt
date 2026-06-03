package plain.bookmaru.domain.member.port.`in`.command

data class NicknameChangeCommand(
    val newNickname: String,
    val username: String
)
