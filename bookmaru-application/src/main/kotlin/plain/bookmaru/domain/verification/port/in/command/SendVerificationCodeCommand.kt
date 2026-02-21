package plain.bookmaru.domain.verification.port.`in`.command

data class SendVerificationCodeCommand(
    val email: String
) {
    init {
        require(email.isNotBlank()) { "이메일은 필수입니다." }
        require(email.contains("@")) { "이메일 형식이 올바르지 않습니다." }
    }
}