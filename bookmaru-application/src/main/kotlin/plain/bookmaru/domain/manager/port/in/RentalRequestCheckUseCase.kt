package plain.bookmaru.domain.manager.port.`in`

import plain.bookmaru.domain.manager.port.`in`.command.RentalRequestCheckCommand
import plain.bookmaru.domain.manager.port.out.result.RentalRequestCheckResult

interface RentalRequestCheckUseCase {
    suspend fun execute(command: RentalRequestCheckCommand): List<RentalRequestCheckResult>?
}