package plain.bookmaru.domain.member.presentation.dto.request

import plain.bookmaru.domain.member.port.`in`.command.ChangePasswordCommand

data class PasswordChangeRequestDto(
    val newPassword: String,
    val existingPassword: String
) {
    fun toCommand(accessToken: String, username: String) : ChangePasswordCommand = ChangePasswordCommand(newPassword, existingPassword, username, accessToken)
}
