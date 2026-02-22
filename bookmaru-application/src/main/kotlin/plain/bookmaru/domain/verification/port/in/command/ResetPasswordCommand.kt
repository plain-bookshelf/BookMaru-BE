package plain.bookmaru.domain.verification.port.`in`.command

data class ResetPasswordCommand(
    val username: String,
    val newPassword: String
)
