package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.ChangePasswordCommand

interface ChangePasswordUseCase {
    suspend fun execute(command: ChangePasswordCommand)
}