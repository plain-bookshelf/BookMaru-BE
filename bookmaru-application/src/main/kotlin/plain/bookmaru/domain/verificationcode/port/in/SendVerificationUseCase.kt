package plain.bookmaru.domain.verificationcode.port.`in`

import plain.bookmaru.domain.verificationcode.port.`in`.command.SendVerificationCodeCommand

interface SendVerificationUseCase {
    suspend fun sendVerificationCode(email : SendVerificationCodeCommand)
}