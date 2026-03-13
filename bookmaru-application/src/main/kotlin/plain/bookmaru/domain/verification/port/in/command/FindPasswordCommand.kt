package plain.bookmaru.domain.verification.port.`in`.command

data class FindPasswordCommand(
    val email: String,
    val verificationCode: String
) {
}