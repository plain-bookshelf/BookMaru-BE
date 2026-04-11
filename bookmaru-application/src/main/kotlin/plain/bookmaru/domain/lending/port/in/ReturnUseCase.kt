package plain.bookmaru.domain.lending.port.`in`

import plain.bookmaru.domain.lending.port.`in`.command.ReturnCommand

interface ReturnUseCase {
    suspend fun execute(command: ReturnCommand)
}