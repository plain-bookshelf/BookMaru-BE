package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand

interface MemberUseCase {
    suspend fun signupMember(command: SignupMemberCommand)
}