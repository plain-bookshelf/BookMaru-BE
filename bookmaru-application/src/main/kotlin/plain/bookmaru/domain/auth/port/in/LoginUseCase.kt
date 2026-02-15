package plain.bookmaru.domain.auth.port.`in`

import plain.bookmaru.domain.auth.port.`in`.command.LoginMemberCommand
import plain.bookmaru.domain.auth.result.TokenResult

interface LoginUseCase {
    suspend fun loginMember(command : LoginMemberCommand) : TokenResult
}