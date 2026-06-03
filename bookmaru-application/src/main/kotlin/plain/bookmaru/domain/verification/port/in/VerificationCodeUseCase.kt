package plain.bookmaru.domain.verification.port.`in`

import plain.bookmaru.domain.verification.port.`in`.command.VerificationCodeCommand

interface VerificationCodeUseCase {
    suspend fun execute(command: VerificationCodeCommand)
}