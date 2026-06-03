package plain.bookmaru.domain.verification.presentation.dto.request

import plain.bookmaru.domain.verification.port.`in`.command.ResetPasswordCommand

data class ResetPasswordRequestDto(
    val newPassword: String,
    val email: String
) {
    fun toCommand(registerToken: String) : ResetPasswordCommand = ResetPasswordCommand(email, newPassword, registerToken)
}
