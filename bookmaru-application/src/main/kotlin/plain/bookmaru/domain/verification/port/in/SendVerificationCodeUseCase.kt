package plain.bookmaru.domain.verification.port.`in`

import plain.bookmaru.domain.verification.port.`in`.command.SendVerificationCodeCommand

interface SendVerificationCodeUseCase {
    suspend fun execute(command : SendVerificationCodeCommand)
}