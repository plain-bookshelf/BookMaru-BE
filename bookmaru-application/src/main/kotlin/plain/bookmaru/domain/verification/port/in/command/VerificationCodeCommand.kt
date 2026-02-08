package plain.bookmaru.domain.verification.port.`in`.command

data class VerificationCodeCommand(
    val email : String,
    val verificationCode : String
) {
    init {
        require(verificationCode.isNotEmpty()) { "인증 코드는 비어있으면 안됩니다." }
        require(verificationCode.length == 6) { "인증 코드는 6자리 입니다." }
    }
}
