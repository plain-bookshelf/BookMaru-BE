package plain.bookmaru.domain.verification.port.`in`

import plain.bookmaru.domain.verification.port.`in`.command.OfficialCodeCommand

interface OfficialCodeCreateUseCase {
    suspend fun execute(command: OfficialCodeCommand)
}