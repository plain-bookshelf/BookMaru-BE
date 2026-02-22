package plain.bookmaru.domain.verification.presentation.dto.request

import plain.bookmaru.domain.verification.port.`in`.command.VerificationCodeCommand

data class VerificationCodeRequestDto(
    val email: String,
    val verificationCode: String
) {
    fun toCommand() = VerificationCodeCommand(email, verificationCode)
}
