package plain.bookmaru.domain.auth.port.`in`

import plain.bookmaru.domain.auth.port.`in`.command.LogoutCommand

interface LogoutUseCase {
    suspend fun execute(command: LogoutCommand)
}