package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.auth.result.TokenResult
import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand

interface SignupUseCase {
    suspend fun signupMember(command: SignupMemberCommand) : TokenResult
}