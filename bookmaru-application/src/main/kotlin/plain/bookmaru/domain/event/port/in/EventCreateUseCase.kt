package plain.bookmaru.domain.event.port.`in`

import plain.bookmaru.domain.event.port.`in`.command.EventCreateCommand

interface EventCreateUseCase {
    suspend fun execute(command: EventCreateCommand)
}