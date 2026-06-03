package plain.bookmaru.domain.lending.port.`in`

import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand

interface CancelReservationUseCase {
    suspend fun execute(command: LendingCommand)
}
