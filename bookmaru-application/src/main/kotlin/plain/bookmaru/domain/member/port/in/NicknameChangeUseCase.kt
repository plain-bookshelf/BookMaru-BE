package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.NicknameChangeCommand

interface NicknameChangeUseCase {
    suspend fun execute(command: NicknameChangeCommand)
}