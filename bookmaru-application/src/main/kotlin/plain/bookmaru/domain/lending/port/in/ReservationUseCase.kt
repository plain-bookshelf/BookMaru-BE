package plain.bookmaru.domain.lending.port.`in`

import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand

interface ReservationUseCase {
    suspend fun execute(command: LendingCommand)
}