package plain.bookmaru.domain.verification.port.`in`

import plain.bookmaru.domain.verification.port.`in`.command.ResetPasswordCommand


interface ResetPasswordUseCase {
    suspend fun execute(command: ResetPasswordCommand)
}