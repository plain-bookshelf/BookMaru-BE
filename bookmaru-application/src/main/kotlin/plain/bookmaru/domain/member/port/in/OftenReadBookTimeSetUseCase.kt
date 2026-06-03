package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.OftenReadBookTimeSetCommand

interface OftenReadBookTimeSetUseCase {
    suspend fun execute(command: OftenReadBookTimeSetCommand)
}