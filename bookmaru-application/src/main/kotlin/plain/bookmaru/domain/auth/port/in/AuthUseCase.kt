package plain.bookmaru.domain.auth.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.SignupMemberCommand

interface AuthUseCase {
    fun signupMember(command : SignupMemberCommand)
}