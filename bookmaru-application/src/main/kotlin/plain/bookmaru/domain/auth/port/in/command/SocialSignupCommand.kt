package plain.bookmaru.domain.auth.port.`in`.command

data class SocialSignupCommand(
    val registerToken: String,
    val affiliationName: String,
    val platformType: String
)