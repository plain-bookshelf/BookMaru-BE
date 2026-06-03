package plain.bookmaru.domain.lending.port.`in`

import plain.bookmaru.domain.lending.port.`in`.command.ApproveRentalRequestCommand

interface ApproveRentalRequestUseCase {
    suspend fun execute(command: ApproveRentalRequestCommand)
}
