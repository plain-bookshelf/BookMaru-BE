package plain.bookmaru.domain.auth.port.`in`

import plain.bookmaru.domain.auth.port.`in`.command.ReissueCommand
import plain.bookmaru.domain.auth.result.TokenResult

interface ReissueUseCase {
    suspend fun reissue(reissueCommand: ReissueCommand) : TokenResult
}