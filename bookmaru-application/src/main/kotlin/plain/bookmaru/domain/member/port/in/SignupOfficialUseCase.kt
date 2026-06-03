package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.auth.port.out.result.TokenResult
import plain.bookmaru.domain.member.port.`in`.command.SignupOfficialCommand

interface SignupOfficialUseCase {
    suspend fun execute(command: SignupOfficialCommand) : TokenResult
}