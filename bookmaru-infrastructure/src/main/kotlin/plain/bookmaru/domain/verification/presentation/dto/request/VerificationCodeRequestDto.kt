package plain.bookmaru.domain.verification.presentation.dto.request

data class VerificationCodeRequestDto(
    val email: String,
    val verificationCode: String
)
