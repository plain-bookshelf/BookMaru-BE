package plain.bookmaru.domain.verification.vo

data class VerificationData(
    val code: String,
    val codeType: VerificationCodeType
)
