package plain.bookmaru.domain.verification.port.`in`

import plain.bookmaru.domain.verification.port.`in`.command.FindPasswordCommand
import plain.bookmaru.domain.verification.port.out.result.RegisterTokenResult

interface FindPasswordUseCase {
    suspend fun execute(command: FindPasswordCommand) : RegisterTokenResult
}