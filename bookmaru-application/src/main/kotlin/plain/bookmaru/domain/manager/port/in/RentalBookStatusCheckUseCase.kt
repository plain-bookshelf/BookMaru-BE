package plain.bookmaru.domain.manager.port.`in`

import plain.bookmaru.common.result.PageResult
import plain.bookmaru.domain.manager.port.`in`.command.RentalBookStatusCheckCommand
import plain.bookmaru.domain.manager.port.out.resullt.RentalBookStatusCheckResult

interface RentalBookStatusCheckUseCase {
    suspend fun execute(command: RentalBookStatusCheckCommand) : PageResult<RentalBookStatusCheckResult>?
}