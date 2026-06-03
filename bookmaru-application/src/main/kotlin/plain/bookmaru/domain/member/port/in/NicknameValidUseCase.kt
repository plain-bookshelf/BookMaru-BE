package plain.bookmaru.domain.member.port.`in`

import plain.bookmaru.domain.member.port.`in`.command.NicknameValidCommand

interface NicknameValidUseCase {
    suspend fun execute(command: NicknameValidCommand): Boolean
}