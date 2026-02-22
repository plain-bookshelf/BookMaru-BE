package plain.bookmaru.domain.verification.port.`in`

import plain.bookmaru.domain.verification.port.`in`.command.FindPasswordCommand

interface FindPasswordUseCase {
    suspend fun execute(command: FindPasswordCommand) : Boolean
}