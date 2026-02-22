package plain.bookmaru.domain.verification.presentation.dto.request

import plain.bookmaru.domain.verification.port.`in`.command.ResetPasswordCommand

data class ResetPasswordRequestDto(
    val newPassword: String,
    val username: String
) {
    fun toCommand() : ResetPasswordCommand = ResetPasswordCommand(username, newPassword)
}
