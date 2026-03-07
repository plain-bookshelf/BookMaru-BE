package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.DeleteMemberCommand

interface DeleteMemberUseCase {
    suspend fun deleteMember(command: DeleteMemberCommand)
}