package plain.bookmaru.domain.event.port.`in`

import plain.bookmaru.domain.event.port.`in`.command.EventDeleteCommand

interface EventDeleteUseCase {
    suspend fun execute(command: EventDeleteCommand)
}