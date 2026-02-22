package plain.bookmaru.domain.auth.port.`in`.command

data class LogoutCommand(
    val accessToken: String,
    val username: String
)
