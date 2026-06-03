package plain.bookmaru.domain.auth.port.`in`

import plain.bookmaru.domain.auth.port.`in`.command.ReissueCommand
import plain.bookmaru.domain.auth.port.out.result.TokenResult

interface ReissueUseCase {
    suspend fun execute(reissueCommand: ReissueCommand) : TokenResult
}