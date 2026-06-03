package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.member.port.`in`.command.AffiliationInfoChangeCommand

interface AffiliationInfoChangeUseCase {
    suspend fun execute(command: AffiliationInfoChangeCommand) : TokenResult
}