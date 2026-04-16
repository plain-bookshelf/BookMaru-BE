package plain.bookmaru.domain.event.port.`in`

import plain.bookmaru.domain.event.port.`in`.command.EventChangeCommand

interface EventChangeUseCase {
    suspend fun execute(command: EventChangeCommand)
}