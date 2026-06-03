package plain.bookmaru.domain.auth.port.`in`

import plain.bookmaru.domain.auth.port.`in`.command.SocialSignupCommand
import plain.bookmaru.domain.auth.port.out.result.TokenResult

interface SocialSignupUseCase {
    suspend fun execute(command: SocialSignupCommand) : TokenResult
}