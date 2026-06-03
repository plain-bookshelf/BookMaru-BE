package plain.bookmaru.domain.verification.presentation.dto.request

import plain.bookmaru.domain.verification.port.`in`.command.FindPasswordCommand

data class FindPasswordRequestDto(
    val email: String,
    val verificationCode: String
) {
    fun toCommand() : FindPasswordCommand = FindPasswordCommand(email, verificationCode)
}