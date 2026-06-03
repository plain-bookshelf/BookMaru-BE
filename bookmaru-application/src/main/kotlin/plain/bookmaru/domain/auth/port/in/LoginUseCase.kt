package plain.bookmaru.domain.auth.port.`in`

import plain.bookmaru.domain.auth.port.`in`.command.LoginMemberCommand
import plain.bookmaru.domain.auth.port.out.result.TokenResult

interface LoginUseCase {
    suspend fun execute(command : LoginMemberCommand) : TokenResult
}