package plain.bookmaru.domain.verification.port.`in`.command

data class ResetPasswordCommand(
    val email: String,
    val newPassword: String,
    val registerToken: String
)
