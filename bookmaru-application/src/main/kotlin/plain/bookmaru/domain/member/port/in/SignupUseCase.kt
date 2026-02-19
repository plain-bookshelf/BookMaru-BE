package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.auth.result.TokenResult
import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand
import plain.bookmaru.domain.officialkey.model.OfficialVerification

interface SignupUseCase {
    suspend fun signupMember(command: SignupMemberCommand) : TokenResult
    suspend fun signupOfficial(command: OfficialVerification) : TokenResult
}