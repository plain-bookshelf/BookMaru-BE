package plain.bookmaru.domain.verification.port.`in`

import plain.bookmaru.domain.verification.port.`in`.command.VerificationCodeCommand
import plain.bookmaru.domain.verification.port.out.result.UsernameResult

interface FindIdUseCase {
    suspend fun execute(command: VerificationCodeCommand) : UsernameResult
}