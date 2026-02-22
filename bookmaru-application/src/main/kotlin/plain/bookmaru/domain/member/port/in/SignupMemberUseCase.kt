package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand

interface SignupMemberUseCase {
    suspend fun execute(command: SignupMemberCommand) : TokenResult
}