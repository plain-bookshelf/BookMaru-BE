package plain.bookmaru.domain.auth.port.`in`.command

data class LogoutCommand(
    val accessToken: String
) {
    companion object {
        fun toCommand(token: String) = LogoutCommand(token)
    }
}
